package oj.constant.dto;

import lombok.Data;

/**
 * 评论点赞的请求DTO（Lombok版）
 */
@Data
public class CommentLikeDTO {

    /**
     * 点赞用户ID（从登录态获取）
     */
    private Integer userId;

    /**
     * 被点赞的评论ID（必填）
     */
    private Integer commentId;
}