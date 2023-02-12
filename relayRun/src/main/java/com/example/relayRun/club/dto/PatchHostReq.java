package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchHostReq {
    @ApiModelProperty(value = "차기 방장 프로필 아이디")
    private Long nextHostProfileIdx;
}
