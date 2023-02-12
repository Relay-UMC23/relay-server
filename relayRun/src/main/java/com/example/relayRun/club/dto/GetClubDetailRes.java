package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetClubDetailRes {
    @ApiModelProperty(example="그룹 idx")
    private Long clubIdx;

    @ApiModelProperty(example="그룹 이름")
    private String name;

    @ApiModelProperty(example="그룹 소개")
    private String content;

    @ApiModelProperty(example="그룹 이미지 url")
    private String imgURL;

    @ApiModelProperty(example="그룹 방장 프로필 idx")
    private Long hostIdx;

    @ApiModelProperty(example="난이도")
    private Integer level;

    @ApiModelProperty(example="목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example="목표 km")
    private Float goal;

    @ApiModelProperty(example="그룹 모집 상태")
    private String recruitStatus;

    @ApiModelProperty(example="그룹 모집 정원")
    private Integer maxNum;

    @ApiModelProperty(example="그룹내 멤버 정보")
    private List<GetMemberOfClubRes> getMemberOfClubResList;
}

