package com.example.relayRun.user.oauth2;

import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.util.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2UserInfo {
    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String imgURL;

}
