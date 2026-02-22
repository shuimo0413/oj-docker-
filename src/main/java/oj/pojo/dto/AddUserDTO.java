package oj.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddUserDTO {
    private String id;
    private String username;
    private String password;

    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}