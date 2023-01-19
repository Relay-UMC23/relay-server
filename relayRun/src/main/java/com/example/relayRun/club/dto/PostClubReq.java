package com.example.relayRun.club.dto;

import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.GoalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostClubReq {
    private Long clubIdx;
    private String name;
    private String content;
    private UserProfileEntity hostIdx;
    private Integer level;
    private GoalType goalType;
    private Float goal;
}
