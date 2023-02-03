package com.example.relayRun.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetDailyRes {
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate date;

    float totalTime;

    float totalDist;

    float avgPace;
}
