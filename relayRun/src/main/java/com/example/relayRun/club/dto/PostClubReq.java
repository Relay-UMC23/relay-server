package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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
    @NotBlank @Size(max = 20)
    private String name;

    @ApiModelProperty(example="그룹 소개", required = true)
    @NotNull @Size(max = 50)
    private String content;

    @ApiModelProperty(example="그룹 대표 이미지", required = true)
    @NotNull
    private String imgURL;

    @ApiModelProperty(example="방장 식별자 (현재 유저의 프로필 식별자)", required = true)
    @NotNull
    private Long hostIdx;

    @ApiModelProperty(example="최대 인원 수 | Integer", required = true)
    @NotNull @Range(min = 1, max = 8)
    private Integer maxNum;

    @ApiModelProperty(example="난이도 | Integer | 전체 : 0, 초급 : 1, 중급 : 2, 상급 : 3", required = true)
    @NotNull @Range(min = 0, max = 3)
    private Integer level;

    @ApiModelProperty(example="그룹 목표 종류 | String | 목표 없음 : NOGOAL, 거리 : DISTANCE, 시간 : TIME")
    private GoalType goalType;

    @ApiModelProperty(example="목표치 Float | 거리 단위 : km, 시간 단위 : 초")
    private Float goal;

    @ApiModelProperty(required = true)
    @NotNull @Size(min = 1)
    private List<TimeTableDTO> timeTable;
}
