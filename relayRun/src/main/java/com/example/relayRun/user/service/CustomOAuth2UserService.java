package com.example.relayRun.user.service;

import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.oauth2.OAuth2Attribute;
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
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User user = super.loadUser(userRequest);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("oAuth2User: " + oAuth2User);
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
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId(google): " + registrationId);

        // 로그인시 키값
        String key = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // user 정보 담긴 attribute
        Map<String, Object> attributes = user.getAttributes();
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.builder()
                .attributes(attributes)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .imgURL((String) attributes.get("picture"))
                .build();

        Optional<UserEntity> savedUser = userRepository.findByEmail(oAuth2Attribute.getEmail());
        if (savedUser.isEmpty()) {
            log.info("회원가입 진행");
            // 유저 회원가입
            UserEntity newUser = UserEntity.builder()
                    .name(oAuth2Attribute.getName())
                    .email(oAuth2Attribute.getEmail())
                    .pwd(new BCryptPasswordEncoder().encode("1234"))
                    .loginType(LoginType.valueOf(registrationId.toUpperCase()))
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

//            return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(newUser.getRole().toString())), oAuth2Attribute.getAttributes(), key);
        }

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), oAuth2Attribute.getAttributes(), key);
        log.info("defaultOauth2User: " + defaultOAuth2User);

        return defaultOAuth2User;
    }
}

