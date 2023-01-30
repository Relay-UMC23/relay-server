package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchProfileReq {
    @ApiModelProperty(value = "변경할 닉네임")
    private String nickName;
    @ApiModelProperty(value = "변경할 프로필 이미지 (아바타 이미지)")
    private String imgUrl;
    @ApiModelProperty(value = "변경할 상태 메세지")
    private String statusMsg;
}
