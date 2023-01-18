package com.example.relayRun.jwt.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

    @ApiModelProperty(example = "권한 타입")
    private String grantType;
    @ApiModelProperty(example = "엑세스 토큰")
    private String accessToken;
    @ApiModelProperty(example = "리프레쉬 토큰")
    private String refreshToken;
    @ApiModelProperty(example = "엑세스 토큰 만료 시간")
    private Long accessTokenExpiresIn;

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}