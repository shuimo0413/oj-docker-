package oj.constant.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_likes")
public class CommentLike {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private Integer commentId;
    private LocalDateTime likedTime;
}
