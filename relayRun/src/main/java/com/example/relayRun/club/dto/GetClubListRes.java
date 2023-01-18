package com.example.relayRun.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public interface GetClubListRes {

    Long getClubIdx();
    String getName();
    String getContent();
    String getImgURL();
    String getRecruitStatus();
}
