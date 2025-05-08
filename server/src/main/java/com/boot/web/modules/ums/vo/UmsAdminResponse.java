package com.boot.web.modules.ums.vo;

import com.boot.web.modules.ums.model.UmsAdmin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsAdminResponse extends UmsAdmin {

    @ApiModelProperty("科室名称")
    private String deptName;

    @ApiModelProperty("科室编号代码")
    private String deptCode;

    @ApiModelProperty("科室描述")
    private String deptDesc;
}
