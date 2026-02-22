package oj.constant;

import java.util.Random;

public final class RedisCacheKeyConstants {

    private RedisCacheKeyConstants() {}

    private static final Random RANDOM = new Random();

    // 评论相关缓存
    /** 评论列表缓存前缀：comment:list:问题ID:页码:页大小 */
    public static final String COMMENT_LIST_KEY_PREFIX = "comment:list:";
    /** 单条评论缓存前缀：comment:detail:评论ID */
    public static final String COMMENT_DETAIL_KEY_PREFIX = "comment:detail:";
    /** 评论列表缓存过期时间（分钟） */
    public static final long COMMENT_LIST_EXPIRE_MINUTES = 10L;
    /** 单条评论缓存过期时间（分钟） */
    public static final long COMMENT_DETAIL_EXPIRE_MINUTES = 30L;
    /** 随机过期时间范围（分钟），防止缓存雪崩 */
    public static final long COMMENT_DETAIL_EXPIRE_RANDOM_MINUTES = 10L;

    //问题相关缓存
    public static final String QUESTION_DETAIL_KEY_PREFIX = "question:detail:";
    public static final long QUESTION_DETAIL_EXPIRE_MINUTES = 15L;
    public static final long QUESTION_DETAIL_EXPIRE_RANDOM_MINUTES = 5L;

    public static long getRandomExpireMinutes(long baseMinutes, long randomMinutes) {
        return baseMinutes + RANDOM.nextLong(randomMinutes);
    }

}