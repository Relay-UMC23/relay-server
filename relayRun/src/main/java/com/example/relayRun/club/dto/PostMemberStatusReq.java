package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "그룹 신청 / 시간표 등록 요청 Model")
public class PostMemberStatusReq {
    @ApiModelProperty(example = "유저의 프로필 인덱스")
    private Long userProfileIdx;

    @ApiModelProperty(example = "시간표 정보")
    private List<TimeTableDTO> timeTables;
}
