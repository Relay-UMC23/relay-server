package com.example.relayRun.user.controller;

import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.service.KakaoService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController (KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @ApiOperation(value = "카카오 소셜 로그인")
    @ResponseBody
    @GetMapping("/auth/kakao/callback")
    public BaseResponse<TokenDto> kakaoLogin(String code) {
        try {
            String accessToken = kakaoService.getAuthorize(code);
            TokenDto token = kakaoService.getProfile(accessToken);
            return new BaseResponse<>(token);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
