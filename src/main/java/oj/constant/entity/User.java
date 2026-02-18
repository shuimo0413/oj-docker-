package oj.constant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.INPUT)
    private String id;
    private String username;
    private String password;
    private String roles;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}