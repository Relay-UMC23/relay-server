package com.example.relayRun.user.oauth2;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String imgURL;

}
