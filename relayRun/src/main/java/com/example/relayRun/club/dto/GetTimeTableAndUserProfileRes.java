package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetTimeTableAndUserProfileRes {
//    @ApiModelProperty(example = "프로필 정보")
//    private GetMemberProfileRes userProfile;

    @ApiModelProperty(example = "유저 인덱스")
    private Long userProfileIdx;

    @ApiModelProperty(example = "유저 닉네임")
    private String nickName;

    @ApiModelProperty(example = "해당 프로필 시간표")
    private List<GetTimeTableRes> timeTables;
}
