package com.example.relayRun.user.dto;

import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.util.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper {
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public UserEntity toEntity(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        return UserEntity.builder()
                .email((String)attributes.get("email"))
                .name((String)attributes.get("name"))
                .pwd(bCryptPasswordEncoder.encode("1234"))
                .loginType(LoginType.NAVER)
                .role(Role.ROLE_USER)
                .build();
    }
}
