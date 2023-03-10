package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProfileRes {
    @ApiModelProperty(example = "유저 프로필 식별자")
    private Long userProfileIdx;
    @ApiModelProperty(example = "닉네임")
    private String nickname;
    @ApiModelProperty(example = "상태메세지")
    private String statusMsg;
    @ApiModelProperty(example = "y")
    private String isAlarmOn;
    @ApiModelProperty(example= "이미지 경로")
    private String imgUrl;
    @ApiModelProperty(example= "유저 이름")
    private String userName;
    @ApiModelProperty(example= "유저 이메일")
    private String email;
    @ApiModelProperty(example = "프로필이 속한 그룹 식별자")
    private Long clubIdx;
    @ApiModelProperty(example = "프로필이 속한 그룹 이름")
    private String clubName;
}
