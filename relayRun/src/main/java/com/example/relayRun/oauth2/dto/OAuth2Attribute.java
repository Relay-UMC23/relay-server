package com.example.relayRun.oauth2.dto;

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

    public static OAuth2Attribute buildInfo(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(attributes);
//            case "kakao":
//                return ofKakao(attributes);
            case "naver":
                return ofNaver(attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .imgURL((String)attributes.get("picture"))
                .attributes(attributes)
                .build();
    }

//    private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
//
//        return OAuth2Attribute.builder()
//                .name((String) kakaoProfile.get("nickname"))
//                .email((String) kakaoAccount.get("email"))
//                .imgURL((String)kakaoProfile.get("profile_image_url"))
//                .attributes(kakaoAccount)
//                .build();
//    }
    private static OAuth2Attribute ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .imgURL((String) response.get("profile_image"))
                .attributes(response)
                .build();
    }


}
