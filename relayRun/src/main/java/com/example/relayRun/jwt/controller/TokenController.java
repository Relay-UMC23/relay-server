package com.example.relayRun.jwt.controller;

import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/token")
public class TokenController {
    private final TokenProvider tokenProvider;

    @GetMapping("/expired")
    public String auth() {
        throw new RuntimeException();
    }

    @GetMapping("/refresh")
    public String refreshAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Refresh");

        if (token != null && tokenProvider.validateToken(token)) {
            String email = String.valueOf(tokenProvider.parseClaims(token));
            Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Auth"));
            TokenDto newToken = tokenProvider.generateTokenDto(authentication);

            response.addHeader("Access", newToken.getAccessToken());
            response.addHeader("Refresh", newToken.getRefreshToken());
            response.setContentType("application/json;charset=UTF-8");

            return "HAPPY NEW TOKEN";
        }

        throw new RuntimeException();
    }
}
