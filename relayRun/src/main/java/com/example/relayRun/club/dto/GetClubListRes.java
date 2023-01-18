package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public interface GetClubListRes {

    @ApiModelProperty(example = "그룹 식별자")
    Long getClubIdx();
    @ApiModelProperty(example = "그룹 이름")
    String getName();
    @ApiModelProperty(example = "그룹 소개")
    String getContent();
    @ApiModelProperty(example = "그룹 이미지")
    String getImgURL();
    @ApiModelProperty(example = "그룹 모집 상태")
    String getRecruitStatus();
}
