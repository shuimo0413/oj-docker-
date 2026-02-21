package oj.constant.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private String message;
    private String id;
    private String username;
    private String password;
    private String token;


    private String roles;



    @JsonIgnore
    private LocalDateTime createTime;
    @JsonIgnore
    private LocalDateTime updateTime;


}
