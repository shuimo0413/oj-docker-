package oj.constant.dto;

import lombok.Data;

/**
 * 新增/回复评论的请求DTO（Lombok版）
 */
@Data // 自动生成getter/setter/toString等
public class CommentDTO {

    /**
     * 用户ID（建议从登录态获取，前端无需传）
     */
    private Integer userId;

    /**
     * 评论内容（必填）
     */
    private String content;

    /**
     * 父评论ID（回复时传，新增评论为null）
     */
    private Integer parentCommentId;

    /**
     * 被评论的帖子ID（必填）
     */
    private Integer questionId;

}