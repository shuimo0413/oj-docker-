package oj.constant.vo;

import lombok.Data;
import java.util.List;

/**
 * 评论的响应VO（Lombok版）
 */
@Data
public class CommentVO {


    private Integer id;

    private Integer userId;

    private String userName;


    private String content;

    /**
     * 父评论ID
     */
    private Integer parentCommentId;

    /**
     * 被评论的帖子ID
     */
    private Integer questionId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 当前登录用户是否已点赞
     */
    private Boolean isLiked;


    /**
     * 子评论列表
     */
    private List<CommentVO> childComments;
}