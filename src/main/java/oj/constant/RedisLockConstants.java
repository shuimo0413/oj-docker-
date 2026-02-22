package oj.constant;

public final class RedisLockConstants {
    private RedisLockConstants() {
        throw new AssertionError("常量类禁止实例化");
    }
    public static final String LOCK_PREFIX = "lock:";
    public static final long DEFAULT_EXPIRE_TIME = 10;
    public static final long DEFAULT_WAIT_TIME = 1000;

}
