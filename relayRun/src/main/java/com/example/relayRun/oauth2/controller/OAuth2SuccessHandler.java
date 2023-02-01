package com.example.relayRun.oauth2.controller;

import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication authentication) throws IOException, ServletException {

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // body로 token 넘기기
        writeTokenResponse(res, tokenDto);

        /* // query로 token 넘기기
        String targetURL = UriComponentsBuilder.fromUriString("/login")
                .queryParam("auth", tokenDto.getAccessToken())
                .queryParam("refresh", tokenDto.getRefreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(req, res, targetURL);
        */
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDto tokenDto) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new BaseResponse<>(tokenDto)));

        /* // header 정보에도 추가
        response.addHeader("Auth", tokenDto.getAccessToken());
        response.addHeader("Refresh", tokenDto.getRefreshToken());
         */
    }

}
