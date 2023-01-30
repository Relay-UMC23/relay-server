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
public class TimeTableDTO {
    @ApiModelProperty(example = "요일")
    private Integer day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "시작 시간")
    private LocalTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "종료 시간")
    private LocalTime end;

    @ApiModelProperty(example = "목표 수치")
    private Float goal;

    @ApiModelProperty(example = "목표 타입")
    private GoalType goalType;
}
