package com.example.relayRun.user.dto;

import lombok.*;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private Long userIdx;
    private String email;
    private String pwd;
    private Role role;

    @Builder
    public PostUserReq(Long userIdx, String email, String pwd){
        this.userIdx = userIdx;
        this.email = email;
        this.pwd = pwd;
    }
}
