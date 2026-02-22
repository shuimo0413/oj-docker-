package oj.pojo.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

// 命名：业务（SubmissionRecord）+ 场景（Rpc）+ 方向（Request）
@Data
public class SubmissionRecordRpcRequest implements Serializable {

    @JsonProperty("source_code")
    private String sourceCode;
    @JsonProperty("language_id")
    private Integer languageId;
    private String stdin;
    @JsonProperty("cpu_time_limit")
    private Float cpuTimeLimit;
    @JsonProperty("memory_limit")
    private Long memoryLimit;
}