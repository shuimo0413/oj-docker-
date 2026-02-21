package oj.constant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@TableName("user_submission_code")
public class UserSubmissionCode {
    @TableId(value ="id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer questionId;

    private String code;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
