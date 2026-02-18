package oj.constant.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)

    private Integer id;
    // 用户ID
    private Integer userId;
    // 评论内容
    private String content;
    // 父评论ID（回复时使用）
    private Integer parentCommentId;
    // 被评论的帖子/内容ID
    private Integer questionId;
    // 点赞数量
    private Integer likeCount;
    // 评论创建时间
    private LocalDateTime createTime;
}
