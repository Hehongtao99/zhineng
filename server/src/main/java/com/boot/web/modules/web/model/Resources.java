package com.boot.web.modules.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 资源表
 * </p>
 *
 * @author aidenz233
 * @since 2025-04-16
 */
@Getter
@Setter
@ApiModel(value = "Resources对象", description = "资源表")
public class Resources implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("资源编号")
    private String code;

    @ApiModelProperty("类型（1:会议室, 2: 设备）")
    private Integer type;

    @ApiModelProperty("资源描述")
    private String description;


}
