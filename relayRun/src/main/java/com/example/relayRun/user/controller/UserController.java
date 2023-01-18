package com.example.relayRun.user.controller;


import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.service.UserService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @ApiOperation(value = "회원가입", notes ="body : name, email, pwd")
    @PostMapping("/sign-in")
    public BaseResponse<TokenDto> signIn(@RequestBody PostUserReq user) {
        try {
            TokenDto token = this.userService.signIn(user);
            return new BaseResponse<>(token);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    @ResponseBody
//    @PostMapping("/logIn")
//    public BaseResponse<TokenDto> logIn(@RequestBody PostUserReq user) {
//        if (user.getEmail().length() == 0 || user.getEmail() == null ) {
//            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
//        }
//        try{
//            TokenDto token = this.userService.logIn(user);
//        }
//    }

}
