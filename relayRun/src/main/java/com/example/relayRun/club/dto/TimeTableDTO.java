package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableDTO {
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
