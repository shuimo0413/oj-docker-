package oj.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPointDTO {
//    对应的题目id
    private String questionId;
    //    测试点输入

    private String input;
    private String output;
    private Integer isSample;


    //    创建时间

    private LocalDateTime createTime;
    //    更新时间
    private LocalDateTime updateTime;
}
