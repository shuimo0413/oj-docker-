package oj.constant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_submission_record")
public class UserSubmissionRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private Integer codeId;

    private String result;
    private Long time;

    private Long memory;
    private String language;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
