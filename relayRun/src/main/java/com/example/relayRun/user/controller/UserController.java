package com.example.relayRun.user.controller;


import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.service.UserService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/sign-in")
    public BaseResponse<TokenDto> signIn(@RequestBody PostUserReq user) {
        try {
            TokenDto token = this.userService.signIn(user);
            return new BaseResponse<>(token);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
