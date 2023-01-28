package com.example.relayRun.record.dto;

import com.example.relayRun.record.entity.LocationEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetRecordByIdxRes {
    private Long recordIdx;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private Float time;

    private Float distance;

    private Float pace;

    private String goalStatus;

    private List<GetLocationRes> locationList;
}
