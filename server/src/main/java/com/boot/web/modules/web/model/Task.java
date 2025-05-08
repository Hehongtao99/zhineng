package com.boot.web.modules.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author aidenz233
 * @since 2025-04-16
 */
@Getter
@Setter
@ApiModel(value = "Task对象", description = "任务表")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("任务标题")
    private String title;

    @ApiModelProperty("任务编码")
    private String code;

    @ApiModelProperty("任务类型ID")
    private Long categoryId;

    @ApiModelProperty("任务描述")
    private String description;

    @ApiModelProperty("任务状态（1:待办, 4:已完成, 5:已逾期）")
    private Integer status;

    @ApiModelProperty("重要性权重设置")
    private Long importanceId;

    @ApiModelProperty("紧急性权重设置")
    private Long exigencyId;

    @ApiModelProperty("任务所需资源数据（JSON格式）")
    private String resourcesData;

    @ApiModelProperty("任务所需人员数据（JSON格式）")
    private String userData;

    @ApiModelProperty("任务所需时间分钟数")
    private Integer timeSpend;

    @ApiModelProperty("任务开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty("任务截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @ApiModelProperty("任务提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderTime;

    @ApiModelProperty("是否需要会议室（1需要、0不需要）")
    private Boolean needMeetingRoom;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;


}
