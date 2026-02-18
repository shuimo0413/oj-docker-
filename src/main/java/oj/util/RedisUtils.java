package oj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 封装了 RedisTemplate 的常用操作
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== Key 相关操作 ====================

    /**
     * 删除单个 key
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 删除多个 key
     * @param keys 键集合
     * @return 删除的 key 数量
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置 key 的过期时间
     * @param key 键
     * @param time 过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, time, timeUnit));
    }

    /**
     * 获取 key 的过期时间
     * @param key 键
     * @param timeUnit 时间单位
     * @return 过期时间
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 判断 key 是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ==================== String 相关操作 ====================

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return 是否放入成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置过期时间
     * @param key 键
     * @param value 值
     * @param time 过期时间
     * @param timeUnit 时间单位
     * @return 是否放入成功
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    // ==================== Hash 相关操作 ====================

    /**
     * Hash 缓存放入
     * @param key 键
     * @param hashKey hash 键
     * @param value 值
     * @return 是否放入成功
     */
    public boolean hset(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash 缓存获取
     * @param key 键
     * @param hashKey hash 键
     * @return 值
     */
    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取 Hash 中所有键值对
     * @param key 键
     * @return 键值对 Map
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除 Hash 中的指定键值对
     * @param key 键
     * @param hashKeys hash 键集合
     */
    public void hdelete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ==================== Set 相关操作 ====================

    /**
     * Set 缓存放入
     * @param key 键
     * @param values 值集合
     * @return 放入的元素数量
     */
    public Long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Set 缓存获取
     * @param key 键
     * @return 值集合
     */
    public Set<Object> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // ==================== List 相关操作 ====================

    /**
     * List 缓存放入（从列表头部插入）
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    public Long lpush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * List 缓存放入（从列表尾部插入）
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    public Long rpush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * List 缓存获取
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 值列表
     */
    public List<Object> lrange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
}