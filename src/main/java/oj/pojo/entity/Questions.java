package oj.constant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("questions")
public class Questions {
    @TableId(type = IdType.INPUT)
    private String id;
    private String title;
    private String label;
    private Integer testPointNum;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


    private Long limitedTime;
}