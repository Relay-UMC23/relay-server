package com.example.relayRun.club.dto;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberStatusRes {
    @ApiModelProperty(example="지원 목록 idx")
    private Long memberStatusIdx;

    @ApiModelProperty(example="지원 상태")
    private String applyStatus;

    @ApiModelProperty(example="지원시 소개 메시지")
    private String comment;

    @ApiModelProperty(example="지원한 프로필 idx")
    private UserProfileEntity userProfileIdx;

    @ApiModelProperty(example="지원한 그룹 idx")
    private ClubEntity clubIdx;
}
