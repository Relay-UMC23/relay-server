package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class GetTimeTableListRes {
    @ApiModelProperty(example = "시간표 인덱스")
    private Long timeTableIdx;

    @ApiModelProperty(example = "요일")
    private Integer day;

    @ApiModelProperty(example = "시작 시간")
    private String start;

    @ApiModelProperty(example = "종료 시간")
    private String end;

    @ApiModelProperty(example = "목표 수치")
    private Float goal;

    @ApiModelProperty(example = "목표 타입")
    private GoalType goalType;
}
