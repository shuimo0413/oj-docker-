package oj.constant.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
@Data
@SuppressWarnings("serial")
public class SubmissionRecordRpcResponse implements Serializable {
    private String stdout;

    private BigDecimal time;
    private Integer memory;

    private String stderr;
    private String token;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;


    private Status status; // 作为映射
    @Data
    public static class Status implements Serializable {
        private Integer id;
        private String description;
    }

}