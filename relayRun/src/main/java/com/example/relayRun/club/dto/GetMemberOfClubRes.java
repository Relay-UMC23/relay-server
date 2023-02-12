package com.example.relayRun.club.dto;

import com.example.relayRun.record.dto.GetRecordWithoutLocationRes;
import com.example.relayRun.user.dto.GetMemberProfileRes;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberOfClubRes {

    @ApiModelProperty(example = "지원 상태 idx")
    private Long memberStatusIdx;

    @ApiModelProperty(example = "프로필 정보")
    private GetMemberProfileRes userProfile;

    @ApiModelProperty(example = "해당 프로필 시간표")
    private GetTimeTableRes timeTableRes;

    @ApiModelProperty(example = "당일 달리기 기록")
    private List<GetRecordWithoutLocationRes> runningRecord;
}
