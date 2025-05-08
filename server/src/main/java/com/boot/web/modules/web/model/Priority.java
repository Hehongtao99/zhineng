package com.boot.web.modules.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 优先级表
 * </p>
 *
 * @author aidenz233
 * @since 2025-04-16
 */
@Getter
@Setter
@ApiModel(value = "Priority对象", description = "优先级表")
public class Priority implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("规则标题")
    private String name;

    @ApiModelProperty("类型（1:重要性, 2: 紧急性）")
    private Integer type;

    @ApiModelProperty("分数")
    private Integer score;

    @ApiModelProperty("权重")
    private BigDecimal weight;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("任务ID")
    private Long categoryId;

    @ApiModelProperty("最小时间区间")
    private Integer minHours;

    @ApiModelProperty("最大时间区间")
    private Integer maxHours;

}
