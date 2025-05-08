package com.boot.web.modules.ums.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * 用户登录参数
 */
@Getter
@Setter
public class UmsAdminParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @NotEmpty
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "用户头像")
    private String icon;
    @Email
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;
    @ApiModelProperty(value = "备注")
    private String note;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "联系方式")
    private String phone;
    @ApiModelProperty(value = "家庭住址")
    private String address;

}
