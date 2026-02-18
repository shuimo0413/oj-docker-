package oj.constant.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddTestQuestionDTO {

    private String id;
    //    测试题目标题
    private String title;
//    测试题目标签
    private String label;
//    测试点数量
    private Integer testPointNum;
//    题目描述
    private String description;
//    创建时间
    private LocalDateTime createTime;
//    更新时间
    private LocalDateTime updateTime;

    //    限时时间
    private Integer limitedTime;
    //    测试点列表
    private List<TestPointDTO> testPointList;
}
