package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchClubInfoReq {
    @ApiModelProperty(example="그룹 이름", required = true)
    private String name;

    @ApiModelProperty(example="그룹 소개", required = true)
    private String content;

    @ApiModelProperty(example="그룹 대표 이미지", required = true)
    private String imgURL;

    @ApiModelProperty(example="최대 인원 수", required = true)
    private Integer maxNum;

    @ApiModelProperty(example="난이도", required = true)
    private Integer level;

    @ApiModelProperty(example="목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example="목표 km")
    private Float goal;

    @ApiModelProperty(value = "변경할 모집 상태")
    private String recruitStatus;
}
