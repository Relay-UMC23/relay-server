package com.example.relayRun.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRunningFinishReq {
    private Long runningRecordIdx;

    private Float distance;

    private Float pace;

    @ApiModelProperty(example = "달린 시간 HH:mm:ss")
    @JsonFormat(pattern= "HH:mm:ss")
    private LocalTime time;

    private List<locationDTO> locations;
}
