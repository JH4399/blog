package com.example.blog.service;

import com.example.blog.config.ImageStorageProperties;
import com.example.blog.exception.BusinessException;
import com.example.blog.vo.ImageUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/** 图片独立于文章存储，文章只保存稳定的访问路径。 */
@Slf4j
@Service
public class ImageStorageService {

    public static final int MAX_FILE_BYTES = 10 * 1024 * 1024;
    private static final int MAX_DIMENSION = 8192;
    private static final long MAX_PIXELS = 20_000_000;
    private static final Pattern FILE_NAME = Pattern.compile("[0-9a-f]{32}\\.(jpg|png|webp)");
    private final Path directory;

    public ImageStorageService(ImageStorageProperties properties) {
        directory = properties.getDirectory().toAbsolutePath().normalize();
    }

    public ImageUploadVO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw BusinessException.badRequest("图片不能为空");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw tooLarge();
        }
        byte[] bytes;
        try (InputStream input = file.getInputStream()) {
            bytes = input.readNBytes(MAX_FILE_BYTES + 1);
        } catch (IOException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "无法读取上传图片", exception);
        }
        if (bytes.length > MAX_FILE_BYTES) {
            throw tooLarge();
        }
        ImageInfo info = inspect(bytes);
        // 文件目录以 年/月 命名
        String folder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        // 随机uuid + 图片后缀
        String name = UUID.randomUUID().toString().replace("-", "") + "." + info.extension();
        store(folder, name, bytes);
        return new ImageUploadVO("/api/images/" + folder + "/" + name,
                originalName(file.getOriginalFilename()), bytes.length,
                info.mediaType().toString(), info.width(), info.height());
    }

    public StoredImage load(String year, String month, String name) {
        if (!year.matches("[0-9]{4}") || !month.matches("0[1-9]|1[0-2]")
                || !FILE_NAME.matcher(name).matches()) {
            throw BusinessException.notFound("图片不存在");
        }
        Path path = directory.resolve(year).resolve(month).resolve(name).normalize();
        try {
            if (!path.startsWith(directory) || !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)
                    || !path.toRealPath().startsWith(directory.toRealPath())) {
                throw BusinessException.notFound("图片不存在");
            }
            String extension = name.substring(name.lastIndexOf('.') + 1);
            return new StoredImage(new FileSystemResource(path), mediaType(extension), Files.size(path));
        } catch (IOException exception) {
            log.warn("读取图片失败", exception);
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "图片暂时无法读取，请稍后重试", exception);
        }
    }

    private static ImageInfo inspect(byte[] bytes) {
        try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes))) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw unsupported();
            }
            ImageReader reader = readers.next();
            try {
                String extension = switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg", "jpg" -> "jpg";
                    case "png" -> "png";
                    case "webp" -> "webp";
                    default -> throw unsupported();
                };
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width < 1 || height < 1 || width > MAX_DIMENSION || height > MAX_DIMENSION
                        || (long) width * height > MAX_PIXELS) {
                    throw BusinessException.badRequest("图片宽高不能超过 8192 像素，总像素不能超过 2000 万");
                }
                // 解码实际内容，不能只相信扩展名、Content-Type 或文件头。
                var decoded = reader.read(0);
                if (decoded == null) {
                    throw BusinessException.badRequest("图片内容无效或已损坏");
                }
                decoded.flush();
                return new ImageInfo(extension, mediaType(extension), width, height);
            } finally {
                reader.dispose();
            }
        } catch (IOException | IllegalArgumentException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "图片内容无效或已损坏", exception);
        }
    }

    private void store(String folder, String name, byte[] bytes) {
        Path temporary = null;
        try {
            Path parent = directory.resolve(folder);
            Files.createDirectories(parent);
            if (!parent.toRealPath().startsWith(directory.toRealPath())) {
                throw new IOException("图片目录超出存储根目录");
            }
            // 写完后再移动到最终名称，读取接口无法访问半成品文件。
            temporary = Files.createTempFile(parent, ".upload-", ".tmp");
            Files.write(temporary, bytes);
            Files.move(temporary, parent.resolve(name));
        } catch (IOException exception) {
            log.error("保存上传图片失败", exception);
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "图片保存失败，请稍后重试", exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException exception) {
                    log.warn("清理上传临时文件失败", exception);
                }
            }
        }
    }

    private static String originalName(String name) {
        if (name == null) {
            return "image";
        }
        String leaf = name.replace('\\', '/');
        leaf = leaf.substring(leaf.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "");
        return leaf.substring(0, Math.min(leaf.length(), 200));
    }

    private static MediaType mediaType(String extension) {
        return switch (extension) {
            case "jpg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> throw unsupported();
        };
    }

    private static BusinessException unsupported() {
        return new BusinessException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "仅支持 JPEG、PNG、WebP 图片");
    }

    private static BusinessException tooLarge() {
        return new BusinessException(HttpStatus.PAYLOAD_TOO_LARGE, "单张图片不能超过 5 MB");
    }

    private record ImageInfo(String extension, MediaType mediaType, int width, int height) { }

    public record StoredImage(Resource resource, MediaType mediaType, long size) { }
}
