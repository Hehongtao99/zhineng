package com.boot.web.modules.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 日程安排表
 * </p>
 *
 * @author aidenz233
 * @since 2025-04-16
 */
@Getter
@Setter
@ApiModel(value = "Schedule对象", description = "日程安排表")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("创建时间")
    private Date createTime;
    
    @ApiModelProperty("用户ID，标识该日程分配给哪个用户")
    private Long userId;
    
    @ApiModelProperty("父日程ID，用于关联主日程和用户日程")
    private Long parentId;

    // 不保存到数据库
    @TableField(exist = false)
    private Double priorityScore;

    // Getter和Setter方法
    public Double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Double priorityScore) {
        this.priorityScore = priorityScore;
    }


}
