package com.boot.web.modules.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通知表
 * </p>
 */
@Getter
@Setter
@ApiModel(value = "Notification对象", description = "通知表")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("接收通知的用户ID")
    private Long userId;

    @ApiModelProperty("通知内容")
    private String message;

    @ApiModelProperty("关联日程安排ID")
    private Long scheduleId;

    @ApiModelProperty("通知类型（1任务提醒、2系统通知等）")
    private Integer type;

    @ApiModelProperty("通知状态（1未读、2已读）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date createTime;


}
