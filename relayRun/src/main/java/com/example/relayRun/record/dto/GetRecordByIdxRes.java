package com.example.relayRun.record.dto;

import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GetRecordByIdxRes {
    private Long recordIdx;

    private String nickName;

    private String clubName;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private Float time;

    private Float distance;

    private Float pace;

    @ApiModelProperty(example = "목표 달성 여부")
    private String goalStatus;

    @ApiModelProperty(example = "목표 종류")
    private GoalType goalType;

    @ApiModelProperty(example = "목표치")
    private Float goalValue;

    private List<GetLocationRes> locationList;
}
