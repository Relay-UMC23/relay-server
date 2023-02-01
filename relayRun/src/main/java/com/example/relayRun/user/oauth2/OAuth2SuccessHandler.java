package com.example.relayRun.user.oauth2;

import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.SocialUserDto;
import com.example.relayRun.user.dto.UserRequestMapper;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        UserEntity userDto = userRequestMapper.toEntity(oAuth2User);
        var attributes = oAuth2User.getAttributes();
        Optional<UserEntity> savedUser = userRepository.findByEmail((String) attributes.get("email"));
        if (savedUser.isEmpty()) {
            log.info("회원가입 진행");
            // 유저 회원가입
            userRepository.save(userDto);

            // 프로필 자동생성
            UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                    .nickName("기본 닉네임")
                    .imgURL((String) attributes.get("profile_image"))
                    .statusMsg("안녕하세요")
                    .userIdx(userDto)
                    .build();
            userProfileRepository.save(userProfileEntity);

            }

        TokenDto token = tokenProvider.generateTokenDto(authentication);
        log.info("{}", token);

        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDto token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Auth", token.getAccessToken());
        response.addHeader("Refresh", token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }
}
