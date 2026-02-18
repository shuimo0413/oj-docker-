package oj.util.model;

import lombok.Data;

@Data
public class JudgeRequest {
    private String code;
    private String language;
    private String input;
    private Long timeout = 10000L;
    private Long memoryLimit = 512L;
}
