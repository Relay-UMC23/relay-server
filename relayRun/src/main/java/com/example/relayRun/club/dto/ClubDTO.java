package com.example.relayRun.club.dto;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
public class ClubDTO {

    @Getter
    @Setter
    @AllArgsConstructor
//    @Builder
    public class Club {
        private Long clubIdx;
        private String name;
        private String content;
        private String imgURL;
        private Long hostIdx;
        private List<MemberStatusEntity> memberStatusEntityList;
        private List<TimeTableEntity> timeTableEntityList;
        private List<RunningRecordEntity> runningRecordEntityList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClubList {
        @ApiModelProperty(example="그룹 idx")
        private Long clubIdx;

        @ApiModelProperty(example="그룹 이름")
        private String name;

        @ApiModelProperty(example="그룹 소개")
        private String content;

        @ApiModelProperty(example="그룹 이미지 url")
        private String imgURL;

        @ApiModelProperty(example="그룹 모집 상태 (모집 중, 모집 완료)")
        private String recruitStatus;
    }

}
