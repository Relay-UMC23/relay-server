package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "시간표 수정 요청 Model")
public class PatchTimeTableListReq {
    @ApiModelProperty(example = "시간표 인덱스")
    private Long timeTableIdx;

    @ApiModelProperty(example = "요일")
    private Integer day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "시작 시간")
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(example = "종료 시간")
    private LocalDateTime end;

    @ApiModelProperty(example = "목표 수치")
    private Float goal;

    @ApiModelProperty(example = "목표 타입")
    private GoalType goalType;
}
