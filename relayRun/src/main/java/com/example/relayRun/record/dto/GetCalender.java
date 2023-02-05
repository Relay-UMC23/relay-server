package com.example.relayRun.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class GetCalender {
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate date;

    Double totalTime;

    Double totalDist;

    Double avgPace;
}
