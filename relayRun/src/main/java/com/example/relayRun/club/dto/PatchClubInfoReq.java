package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchClubInfoReq {
    @ApiModelProperty(example="그룹 이름", required = true)
    @NotBlank @Size(max = 20)
    private String name;

    @ApiModelProperty(example="그룹 소개", required = true)
    @NotNull @Size(max = 50)
    private String content;

    @ApiModelProperty(example="그룹 대표 이미지", required = true)
    @NotNull
    private String imgURL;

    @ApiModelProperty(example="최대 인원 수", required = true)
    @NotNull @Range(min = 1, max = 8)
    private Integer maxNum;

    @ApiModelProperty(example="난이도", required = true)
    @NotNull @Range(min = 0, max = 3)
    private Integer level;

    @ApiModelProperty(example="목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example="목표 km")
    @Positive
    private Float goal;

    @ApiModelProperty(value = "변경할 모집 상태")
    private String recruitStatus;
}
