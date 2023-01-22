package com.example.relayRun.user.controller;


import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.GetUserRes;
import com.example.relayRun.user.dto.PatchUserPwdReq;
import com.example.relayRun.user.dto.PostLoginReq;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.service.UserService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @ApiOperation(value = "회원가입", notes ="비밀번호 validation 규칙은 8글자 이상 16글자 이하, 문자 + 숫자 섞어서입니다!")
    @PostMapping("/sign-in")
    public BaseResponse<TokenDto> signIn(@RequestBody PostUserReq user) {
        try {
            TokenDto token = this.userService.signIn(user);
            return new BaseResponse<>(token);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @ApiOperation(value = "로그인", notes ="bearer Token에 access Token 넣어주세요!")
    @PostMapping("/logIn")
    public BaseResponse<TokenDto> logIn(@RequestBody PostLoginReq user) {
        if (user.getEmail().length() == 0 || user.getEmail() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        try {
            TokenDto token = this.userService.logIn(user);
            return new BaseResponse<>(token);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/")
    public BaseResponse<GetUserRes> getInfo(Principal principal) {
        try{
            GetUserRes result = userService.getUserInfo(principal);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @ApiOperation(value = "비밀번호 변경", notes ="헤더에 access token 담아주세용" +
            " 비밀번호 validation 규칙은 8글자 이상 16글자 이하, 문자 + 숫자 섞어서입니다!")
    @PatchMapping("/pwd")
    public BaseResponse<String> changePwd(Principal principal, @RequestBody PatchUserPwdReq userPwd) {
        try {
            this.userService.changePwd(principal, userPwd);
            return new BaseResponse<>("비밀번호 변경에 성공하였습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
