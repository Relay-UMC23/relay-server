package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "멤버 강퇴 / 그룹 나가기 모델")
public class PatchDeleteMemberReq {
    @ApiModelProperty(example = "유저 프로필 idx", required = true)
    @Positive
    private Long userProfileIdx;
}
