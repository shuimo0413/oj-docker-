package oj.constant.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestQuestionVO {
    private String id;
    //    测试题目标题
    private String title;
    //    测试题目标签
    private String label;
    //    测试点数量
    private Integer testPointNum;
    //    题目描述
    private String description;

}
