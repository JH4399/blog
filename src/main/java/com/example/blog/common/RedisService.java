package com.example.blog.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公共 Redis 操作，统一使用字符串序列化，便于其他服务或运维工具读取。
 * 对象请先显式转换为 JSON；不存在的值返回 null，连接等异常向调用方传播。
 * 列表、哈希和集合默认不设置过期时间，需要时由调用方使用 expire 设置。
 */
@Component
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** SET 同时携带 TTL，避免先写值再设置过期造成永久残留。 */
    public void set(String key, String value, Duration ttl) {
        requirePositiveTtl(ttl);
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /** 原子 SET NX + TTL，仅在 key 不存在时写入。 */
    public boolean setIfAbsent(String key, String value, Duration ttl) {
        requirePositiveTtl(ttl);
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, ttl));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public long delete(Collection<String> keys) {
        if (keys.isEmpty()) {
            return 0;
        }
        Long deleted = redisTemplate.delete(keys);
        return deleted == null ? 0 : deleted;
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean expire(String key, Duration ttl) {
        requirePositiveTtl(ttl);
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
    }

    /** 剩余秒数：-2 表示不存在，-1 表示没有过期时间。 */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /** 原子增减；新建计数器默认无 TTL，已有 key 的 TTL 不变。 */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public void hashPut(String key, String field, String value) {
        redisTemplate.<String, String>opsForHash().put(key, field, value);
    }

    public String hashGet(String key, String field) {
        return redisTemplate.<String, String>opsForHash().get(key, field);
    }

    public Map<String, String> hashEntries(String key) {
        return redisTemplate.<String, String>opsForHash().entries(key);
    }

    public Long hashDelete(String key, String... fields) {
        return redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    public Long listRightPush(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /** 闭区间，end=-1 表示到列表末尾。 */
    public List<String> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public String listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Long setAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Set<String> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Long setRemove(String key, String... values) {
        return redisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    private static void requirePositiveTtl(Duration ttl) {
        if (ttl == null || ttl.isNegative() || ttl.isZero() || ttl.toMillis() < 1) {
            throw new IllegalArgumentException("Redis 过期时间必须至少为 1 毫秒");
        }
    }
}
