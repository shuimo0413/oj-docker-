package oj.pojo.vo;

import lombok.Data;
import oj.pojo.entity.QuestionResult;

import java.util.List;

@Data
public class SubmitTestQuestionVO {
    //    题目的id
    private String id;
//    各个测试点评测结果
    private List<QuestionResult> questionResultList;
}
