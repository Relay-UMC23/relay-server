package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class PostEmailReq {
    @Email
    @ApiModelProperty(example = "이메일 발신자, 서버에게 문의해주세요")
    private String email;
}
