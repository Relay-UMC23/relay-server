package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "멤버 강퇴 모델")
public class PatchDeleteMemberReq {
    @ApiModelProperty(example = "강퇴 유저 프로필 idx", required = true)
    private Long userProfileIdx;
}
