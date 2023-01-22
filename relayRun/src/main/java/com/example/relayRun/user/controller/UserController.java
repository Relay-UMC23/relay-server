package com.example.relayRun.user.controller;


import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.GetUserRes;
import com.example.relayRun.user.dto.GetUserProfileClubRes;
import com.example.relayRun.user.dto.PostLoginReq;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.service.UserProfileService;
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
    private UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @ResponseBody
    @ApiOperation(value = "회원가입", notes ="body값에 name, email, pwd 넣어주세요")
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

    @ApiOperation(value = "속한 그룹의 이름 가져오기", notes ="profile의 id를 query string으로 전달 해주세요")
    @GetMapping("/clubs/accepted")
    public BaseResponse<GetUserProfileClubRes> getUsersClub(@RequestParam("id") Long userProfileIdx) {
        try{
            GetUserProfileClubRes result = userProfileService.getUserProfileClub(userProfileIdx);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
