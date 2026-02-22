package oj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static oj.constant.RedisLockConstants.DEFAULT_EXPIRE_TIME;
import static oj.constant.RedisLockConstants.LOCK_PREFIX;

@Component
public class RedisLockUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    private static final String LOCK_PREFIX = "lock:";
//    private static final long DEFAULT_EXPIRE_TIME = 10;
//    private static final long DEFAULT_WAIT_TIME = 1000;

    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, Thread.currentThread().getId(), expireTime, timeUnit);
        return Boolean.TRUE.equals(success);
    }

//    我想尝试获取锁
    public boolean tryLock(String key, long waitTime, long expireTime, TimeUnit timeUnit) {
        String lockKey = LOCK_PREFIX + key;
        long startTime = System.currentTimeMillis();
        long waitMillis = timeUnit.toMillis(waitTime);
        
        while (true) {
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, Thread.currentThread().getId(), expireTime, timeUnit);
            if (Boolean.TRUE.equals(success)) {
                return true;
            }
            
            if (System.currentTimeMillis() - startTime > waitMillis) {
                return false;
            }
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }
}
