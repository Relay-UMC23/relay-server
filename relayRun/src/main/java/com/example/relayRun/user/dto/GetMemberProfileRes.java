package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMemberProfileRes {
    @ApiModelProperty(example = "유저 프로필 식별자")
    private Long userProfileIdx;
    @ApiModelProperty(example = "닉네임")
    private String nickname;
    @ApiModelProperty(example = "상태메세지")
    private String statusMsg;
    @ApiModelProperty(example= "이미지 경로")
    private String imgUrl;
}
