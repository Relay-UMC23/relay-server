package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class GetTimeTableListRes {
    private Long timeTableIdx;
    private Integer day;
    private LocalTime start;
    private LocalTime end;
    private Float goal;
    private GoalType goalType;
}
