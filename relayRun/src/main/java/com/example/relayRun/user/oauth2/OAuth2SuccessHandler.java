package com.example.relayRun.user.oauth2;

import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication authentication) throws IOException, ServletException {
        log.info("authentication success");
//        log.info("req: " + req);
//        log.info("res: ", res);
        log.info("auth: ", authentication);

//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        log.info("token: ", tokenDto.getAccessToken());

        String targetURL = UriComponentsBuilder.fromUriString("/login")
                .queryParam("auth", tokenDto.getAccessToken())
                .queryParam("refresh", tokenDto.getRefreshToken())
                .build().toUriString();
        log.info("targetURL: ", targetURL);

        getRedirectStrategy().sendRedirect(req, res, targetURL);
    }

}
