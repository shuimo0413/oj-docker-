package oj.constant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_points")
public class TestPoint {
    private Long id;
    private String questionId;
    private String input;
    private String output;
    private Integer isSample;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
