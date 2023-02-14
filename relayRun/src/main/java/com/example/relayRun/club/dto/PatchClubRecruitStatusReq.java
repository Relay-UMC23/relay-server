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
public class PatchClubRecruitStatusReq {
    @ApiModelProperty(value = "프로필 아이디")
    private Long userProfileIdx;

    @ApiModelProperty(value = "변경할 모집 상태")
    private String recruitStatus;
}
