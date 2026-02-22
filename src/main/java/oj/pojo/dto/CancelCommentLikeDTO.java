package oj.pojo.dto;

import lombok.Data;

@Data
public class CancelCommentLikeDTO {
    private Integer userId;
    private Integer commentId;
}
