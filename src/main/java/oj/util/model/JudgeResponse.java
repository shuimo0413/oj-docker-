package oj.util.model;

import lombok.Data;

@Data
public class JudgeResponse {
    private String status;
    private String output;
    private String error;
    private Long executionTime;
    private Long memoryUsed;
}
