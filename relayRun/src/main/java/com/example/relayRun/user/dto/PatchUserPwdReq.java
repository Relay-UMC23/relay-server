package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserPwdReq {
    @ApiModelProperty(value = "새 비밀번호")
    private String newPwd;
    @ApiModelProperty(value = "새 비밀번호 확인")
    private String newPwdCheck;
}
