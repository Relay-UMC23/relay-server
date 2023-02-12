package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class GetTimeTableRes {
    @ApiModelProperty(example = "시간표 인덱스")
    private Long timeTableIdx;

    @ApiModelProperty(example = "요일")
    private Integer day;

    @JsonFormat(pattern="HH:mm:ss")
    @ApiModelProperty(example = "시작 시간")
    private LocalTime start;

    @JsonFormat(pattern="HH:mm:ss")
    @ApiModelProperty(example = "종료 시간")
    private LocalTime end;

    @ApiModelProperty(example = "목표 수치")
    private Float goal;

    @ApiModelProperty(example = "목표 타입")
    private GoalType goalType;
}
