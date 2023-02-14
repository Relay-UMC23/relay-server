package com.example.relayRun.record.dto;

import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GetDailyRes {
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate date;

    @ApiModelProperty(example = "총 시간")
    private Double totalTime;

    @ApiModelProperty(example = "총 거리")
    private Double totalDist;

    @ApiModelProperty(example = "평균 속도")
    private Double avgPace;

    @ApiModelProperty(example = "목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example = "목표치")
    private Float goalValue;
}
