package com.floating.hikaru.module.system.controller.admin.oauth2.vo.client;

import com.floating.hikaru.framework.common.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("管理后台 - OAuth2 客户端分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientPageReqVO extends PageParam {

    @ApiModelProperty(value = "应用名", example = "土豆", notes = "模糊匹配")
    private String name;

    @ApiModelProperty(value = "状态", example = "1", notes = "参见 CommonStatusEnum 枚举")
    private Integer status;

}
