package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostMemberStatusReq {
    @ApiModelProperty(example = "유저의 프로필 인덱스")
    private Long userProfileIdx;

    private List<TimeTableDTO> timeTables;
}
