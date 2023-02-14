package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeTableDTO {
    @ApiModelProperty(example = "요일 | Integer | 월 : 1, ... 일 : 7")
    private Integer day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "시작 시간 | String | HH:mm:ss")
    private LocalTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "종료 시간 | String | HH:mm:ss")
    private LocalTime end;

    @ApiModelProperty(example = "목표치 | Float | 거리 : km, 시간 : 초")
    private Float goal;

    @ApiModelProperty(example = "목표 종류 | String | 목표 없음 : NOGOAL, 거리 : DISTANCE, 시간 : TIME")
    private GoalType goalType;
}
