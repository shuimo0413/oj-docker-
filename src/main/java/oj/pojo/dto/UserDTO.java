package oj.constant.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String password;

    private String roles;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
