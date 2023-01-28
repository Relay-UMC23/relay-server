package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "시간표 등록 요청 Model")
public class PostTimeTableReq {
    @ApiModelProperty(example = "유저의 신청(member_status) 인덱스")
    private Long memberStatusIdx;

    @ApiModelProperty(example = "시간표 정보")
    private List<TimeTableDTO> timeTables;
}
