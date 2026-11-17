package com.example.blog.mapper;

import com.example.blog.dto.LinkListQuery;
import com.example.blog.dto.ListQuery;
import com.example.blog.dto.PostListQuery;
import com.example.blog.dto.SearchListQuery;
import com.example.blog.dto.UserListQuery;
import com.example.blog.vo.CategoriesVO;
import com.example.blog.vo.LinksVO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.PostListVO;
import com.example.blog.vo.SettingsVO;
import com.example.blog.vo.TagsVO;
import com.example.blog.vo.UserListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPageMapper {
    private final JdbcTemplate jdbc;
    private static final String POST_FROM = " FROM \"BLOG\".\"posts\" p LEFT JOIN \"BLOG\".\"users\" u ON u.\"id\"=p.\"user_id\" LEFT JOIN \"BLOG\".\"categories\" c ON c.\"id\"=p.\"category_id\"";
    private static final String POST_FIELDS = columns("p", "id", "user_id", "category_id", "title", "slug", "status", "is_top", "view_count", "like_count", "comment_count", "created_at") + ", COALESCE(u.\"nickname\",u.\"username\") AS \"author_name\",c.\"name\" AS \"category_name\"";

    public PageResult<PostListVO> selectPostsPage(PostListQuery query, Long visibleAuthorId) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE p.\"deleted_at\" IS NULL");
        equal(where, args, "p.\"status\"", query.getStatus());
        equal(where, args, "p.\"category_id\"", query.getCategoryId());
        equal(where, args, "p.\"user_id\"", query.getAuthorId());
        equal(where, args, "p.\"user_id\"", visibleAuthorId);
        search(where, args, query.getQ(), "p.\"title\"", "p.\"slug\"", "u.\"nickname\"", "u.\"username\"", "CAST(p.\"id\" AS VARCHAR(30))");
        String order = switch (query.getSort() == null ? "default" : query.getSort()) {
            case "views" -> "p.\"view_count\" DESC,p.\"id\" DESC";
            case "likes" -> "p.\"like_count\" DESC,p.\"id\" DESC";
            case "newest" -> "p.\"created_at\" DESC,p.\"id\" DESC";
            default -> "p.\"is_top\" DESC,p.\"created_at\" DESC,p.\"id\" DESC";
        };
        return selectPage(POST_FIELDS, POST_FROM, where, order, args, query, PostListVO.class);
    }

    public PageResult<UserListVO> selectUsersPage(UserListQuery query) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        equal(where, args, "r.\"status\"", query.getStatus());
        equal(where, args, "r.\"role\"", query.getRole());
        search(where, args, query.getQ(), "r.\"username\"", "r.\"nickname\"", "r.\"email\"", "CAST(r.\"id\" AS VARCHAR(30))");
        String fields = columns("r", "id", "username", "nickname", "email", "avatar", "bio", "role", "status", "created_at", "updated_at")
                + ", (SELECT COUNT(*) FROM \"BLOG\".\"posts\" p WHERE p.\"user_id\"=r.\"id\" AND p.\"deleted_at\" IS NULL) AS \"post_count\"";
        return selectPage(fields, " FROM \"BLOG\".\"users\" r", where,
                "r.\"created_at\" DESC,r.\"id\" DESC", args, query, UserListVO.class);
    }

    public PageResult<CategoriesVO> selectCategoriesPage(SearchListQuery query) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        search(where, args, query.getQ(), "r.\"name\"", "r.\"slug\"");
        return selectPage(columns("r", "id", "name", "slug", "description", "sort", "created_at", "updated_at"),
                " FROM \"BLOG\".\"categories\" r", where, "r.\"sort\" ASC,r.\"id\" ASC", args, query, CategoriesVO.class);
    }

    public PageResult<TagsVO> selectTagsPage(SearchListQuery query) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        search(where, args, query.getQ(), "r.\"name\"", "r.\"slug\"");
        return selectPage(columns("r", "id", "name", "slug", "created_at"),
                " FROM \"BLOG\".\"tags\" r", where, "r.\"id\" ASC", args, query, TagsVO.class);
    }

    public PageResult<LinksVO> selectLinksPage(LinkListQuery query) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        equal(where, args, "r.\"status\"", query.getStatus());
        search(where, args, query.getQ(), "r.\"name\"", "r.\"url\"");
        return selectPage(columns("r", "id", "name", "url", "logo", "description", "sort", "status", "created_at"),
                " FROM \"BLOG\".\"links\" r", where, "r.\"sort\" ASC,r.\"id\" ASC", args, query, LinksVO.class);
    }

    public PageResult<SettingsVO> selectSettingsPage(SearchListQuery query) {
        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        search(where, args, query.getQ(), "r.\"config_key\"", "r.\"remark\"");
        return selectPage(columns("r", "id", "config_key", "config_value", "remark", "updated_at"),
                " FROM \"BLOG\".\"settings\" r", where, "r.\"id\" ASC", args, query, SettingsVO.class);
    }

    /** SQL identifiers and ordering are supplied exclusively by the resource methods above. */
    private <T> PageResult<T> selectPage(String fields, String from, StringBuilder where, String order,
            List<Object> args, ListQuery query, Class<T> recordType) {
        Long count = jdbc.queryForObject("SELECT COUNT(*)" + from + where, Long.class, args.toArray());
        long total = count == null ? 0 : count;
        long pages = Math.max(1, (total + query.getSize() - 1) / query.getSize());
        int page = (int) Math.min(query.getPage(), pages);
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add((long) (page - 1) * query.getSize());
        pageArgs.add(query.getSize());
        // SQL-standard pagination supported by DM8 and the Oracle-mode test database.
        // Typed mapping converts CLOBs and timestamps using the declared VO property types.
        List<T> records = jdbc.query("SELECT " + fields + from + where + " ORDER BY " + order
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", BeanPropertyRowMapper.newInstance(recordType), pageArgs.toArray());
        return new PageResult<>(records, total, page, query.getSize(), pages);
    }

    public Map<String, Object> selectSummary() {
        Map<String, Object> result = new LinkedHashMap<>(jdbc.queryForMap("SELECT COUNT(*) AS \"posts\",COALESCE(SUM(CASE WHEN \"status\"=1 THEN 1 ELSE 0 END),0) AS \"published\",COALESCE(SUM(\"view_count\"),0) AS \"views\",COALESCE(SUM(\"like_count\"),0) AS \"likes\" FROM \"BLOG\".\"posts\" WHERE \"deleted_at\" IS NULL"));
        result.putAll(jdbc.queryForMap("SELECT COUNT(*) AS \"users\",COALESCE(SUM(CASE WHEN \"status\"=1 THEN 1 ELSE 0 END),0) AS \"activeUsers\" FROM \"BLOG\".\"users\""));
        for (String resource : List.of("categories", "tags")) {
            result.put(resource, jdbc.queryForObject("SELECT COUNT(*) FROM \"BLOG\".\"" + resource + "\"", Long.class));
        }
        PostListQuery query = new PostListQuery();
        query.setSort("newest");
        result.put("recent", selectPostsPage(query, null).records());
        return result;
    }

    private static void equal(StringBuilder where, List<Object> args, String column, Object value) {
        if (value != null) {
            where.append(" AND ").append(column).append("=?");
            args.add(value);
        }
    }

    private static void search(StringBuilder where, List<Object> args, String keyword, String... columns) {
        if (keyword == null || keyword.isBlank()) return;
        String escaped = keyword.trim().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
        where.append(" AND (");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) where.append(" OR ");
            where.append("LOWER(").append(columns[i]).append(") LIKE ? ESCAPE '!'");
            args.add("%" + escaped + "%");
        }
        where.append(')');
    }

    private static String columns(String alias, String... names) {
        return String.join(",", Arrays.stream(names).map(name -> alias + ".\"" + name + "\"").toList());
    }
}
