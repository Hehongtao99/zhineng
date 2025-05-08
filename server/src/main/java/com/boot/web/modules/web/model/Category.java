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
 * 类别表
 * </p>
 *
 * @author aidenz233
 * @since 2025-03-27
 */
@Getter
@Setter
@ApiModel(value = "Category对象", description = "类别表")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("类别名称")
    private String name;

    @ApiModelProperty("类别描述")
    private String description;

}
