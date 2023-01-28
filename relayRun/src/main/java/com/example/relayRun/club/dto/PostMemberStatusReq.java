package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "그룹 신청 요청 Model")
public class PostMemberStatusReq {
    @ApiModelProperty(example = "유저의 프로필 인덱스")
    private Long userProfileIdx;
}
