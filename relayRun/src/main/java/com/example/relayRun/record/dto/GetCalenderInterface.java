package com.example.relayRun.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface GetCalenderInterface {
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate date();

    Float getTotalTime();

    Float getTotalDist();

    Float getAvgPace();
}
