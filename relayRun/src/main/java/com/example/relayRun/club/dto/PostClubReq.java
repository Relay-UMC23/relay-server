package com.example.relayRun.club.dto;

import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "그룹 생성 Model", description = "hostIdx는 그룹을 만드려고 하는 유저의 프로필 식별자를 넣으면 됩니다!")
public class PostClubReq {

    @ApiModelProperty(example="그룹 식별자", hidden = true)
    private Long clubIdx;

    @ApiModelProperty(example="그룹 이름", required = true)
    private String name;

    @ApiModelProperty(example="그룹 소개", required = true)
    private String content;

    @ApiModelProperty(example="그룹 대표 이미지", required = true)
    private String imgURL;

    @ApiModelProperty(example="방장 식별자", hidden = true)
    private UserProfileEntity hostIdx;

    @ApiModelProperty(example="최대 인원 수", required = true)
    private Integer maxNum;

    @ApiModelProperty(example="난이도", required = true)
    private Integer level;

    @ApiModelProperty(example="목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example="목표 km")
    private Float goal;

    @ApiModelProperty(required = true)
    private List<TimeTableDTO> timeTable;
}
