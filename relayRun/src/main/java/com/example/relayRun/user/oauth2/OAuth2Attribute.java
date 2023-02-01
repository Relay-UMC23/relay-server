package com.example.relayRun.user.oauth2;

import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.util.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String pwd;
    private String imgURL;
    private LoginType loginType;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public static OAuth2Attribute of(String provider, String attributeKey,
                                     Map<String, Object> attributes) {
        switch (provider) {
//            case "google":
//                return ofGoogle(attributeKey, attributes);
//            case "kakao":
//                return ofKakao("email", attributes);
            case "naver":
                return ofNaver("id", attributes);
            default:
                throw new RuntimeException();
        }
    }

//    private static OAuth2Attribute ofGoogle(String attributeKey,
//                                            Map<String, Object> attributes) {
//        return OAuth2Attribute.builder()
//                .name((String) attributes.get("name"))
//                .email((String) attributes.get("email"))
//                .picture((String)attributes.get("picture"))
//                .attributes(attributes)
//                .attributeKey(attributeKey)
//                .build();
//    }
//
//    private static OAuth2Attribute ofKakao(String attributeKey,
//                                           Map<String, Object> attributes) {
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
//
//        return OAuth2Attribute.builder()
//                .name((String) kakaoProfile.get("nickname"))
//                .email((String) kakaoAccount.get("email"))
//                .picture((String)kakaoProfile.get("profile_image_url"))
//                .attributes(kakaoAccount)
//                .attributeKey(attributeKey)
//                .build();
//    }

    private static OAuth2Attribute ofNaver(String attributeKey,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .imgURL((String) response.get("profile_image"))
                .attributes(response)
                .attributeKey(attributeKey)
                .loginType(LoginType.NAVER)
                .build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("name", name);
        map.put("email", email);
        map.put("pwd", pwd);
        map.put("profile_image", imgURL);

        return map;
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .name(name)
                .email(email)
                .pwd(bCryptPasswordEncoder.encode("1234"))
                .loginType(LoginType.NAVER)
                .role(Role.ROLE_USER)
                .build();
    }
}
