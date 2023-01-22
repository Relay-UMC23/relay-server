package com.example.relayRun.club.dto;

import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostClubReq {
    @ApiModelProperty(example="그룹 식별자")
    private Long clubIdx;
    @ApiModelProperty(example="그룹 이름")
    private String name;
    @ApiModelProperty(example="그룹 소개")
    private String content;
    @ApiModelProperty(example="그룹 대표 이미지")
    private String imgURL;
    @ApiModelProperty(example="방장 식별자")
    private UserProfileEntity hostIdx;
    @ApiModelProperty(example="최대 인원 수")
    private Integer maxNum;
    @ApiModelProperty(example="난이도")
    private Integer level;
    @ApiModelProperty(example="목표 종류")
    private GoalType goalType;
    @ApiModelProperty(example="목표 km")
    private Float goal;
}
