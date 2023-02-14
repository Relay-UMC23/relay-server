package com.example.relayRun.record.dto;

import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRunningInitRes {
    private Long runningRecordIdx;
    private GoalType goalType;
    private Float goal;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm:ss")
    private LocalTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm:ss")
    private LocalTime end;
}
