package com.example.relayRun.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SocialUserDto {
    private String email;
    private String name;
    private String pwd;

    @Builder
    public SocialUserDto(String email, String name, String pwd) {
        this.email = email;
        this.name = name;
        this.pwd = pwd;
    }
}
