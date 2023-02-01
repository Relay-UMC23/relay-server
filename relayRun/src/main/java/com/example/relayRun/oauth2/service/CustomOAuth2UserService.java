package com.example.relayRun.oauth2.service;

import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.oauth2.dto.OAuth2Attribute;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        try {
            return process(userRequest, oAuth2User);
//        } catch (AuthenticationException ae) {
//            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        // 진행중인 서비스 구분 (ex. Google, Naver, Kakao ...)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 로그인시 키값
        String key = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // user 정보 담긴 attribute object 생성
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.buildInfo(provider, user.getAttributes());

        Optional<UserEntity> savedUser = userRepository.findByEmail(oAuth2Attribute.getEmail());
        if (savedUser.isEmpty()) {
            log.info("회원가입 진행");
            // 유저 회원가입
            UserEntity newUser = UserEntity.builder()
                    .name(oAuth2Attribute.getName())
                    .email(oAuth2Attribute.getEmail())
                    .pwd(new BCryptPasswordEncoder().encode("1234"))
                    .loginType(LoginType.valueOf(provider.toUpperCase()))
                    .role(Role.ROLE_USER)
                    .build();
            newUser = userRepository.save(newUser);

            // 프로필 자동생성
            UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                    .nickName("기본 닉네임")
                    .imgURL(oAuth2Attribute.getImgURL())
                    .statusMsg("안녕하세요")
                    .userIdx(newUser)
                    .build();
            userProfileRepository.save(userProfileEntity);
        }

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), oAuth2Attribute.getAttributes(), key);

        return defaultOAuth2User;
    }
}

