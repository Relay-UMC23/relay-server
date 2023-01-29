package com.example.relayRun.record.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetRecordWithoutLocationRes {
    private Long recordIdx;
    private LocalDateTime date;
    private String runningStatus;
}
