package oj.pojo.dto;

import lombok.Data;

@Data
public class SubmitTestQuestionDTO {
//    题目的id
    private String id;
    //    用户的id
    private String userId;
//    答案的语言
    private String language;
    //    题目的答案
    private String answer;
}
