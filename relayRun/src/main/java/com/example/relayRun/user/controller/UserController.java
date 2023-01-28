package com.example.relayRun.user.controller;


import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.*;
import com.example.relayRun.user.service.UserProfileService;
import com.example.relayRun.user.service.UserService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@Api(tags = {"유저 관련 API (회원가입/로그인 등)"})
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;
    private UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
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
    @ApiOperation(value = "로그인", notes ="이메일과 비밀번호만 입력해주세요!!")
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

    @ResponseBody
    @DeleteMapping("/withdrawal")
    public BaseResponse<String> deleteUser(Principal principal) throws BaseException {
        this.userService.deleteUser(principal);
        return new BaseResponse<>("회원 탈퇴를 완료했습니다.");
    }

    @ApiOperation(value = "유저 정보 받아오기", notes = "토큰 정보를 통해 해당 유저의 이메일과 이름을 받아옵니다")
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

    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 200, message = "프로필 목록 조회 완료"),
            @ApiResponse(code = 401, message = "권한을 찾을 수 없습니다"),
            @ApiResponse(code = 404, message = "서버 문제 발생"),
            @ApiResponse(code = 500, message = "페이지를 찾을 수 없습니다")
    })
    @ApiOperation(value = "유저 프로필 목록 조회", notes ="유저가 생성한 프로필 리스트 조회하는 API\n" +
            "헤더에 access token 넣어주세요!")
    @GetMapping("/profileList")
    public BaseResponse<List<GetProfileListRes>> viewProfile(Principal principal) {
        try{
            List<GetProfileListRes> getProfileRes = userProfileService.viewProfile(principal);
            return new BaseResponse<>(getProfileRes);
        }
        catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="프로필 신규 생성", notes="닉네임, 상태 메세지, 프로필 알림 설정(y or n), 프로필 사진 경로")
    @PostMapping("/profile")
    public BaseResponse<Long> addProfile(Principal principal, @RequestBody PostProfileReq profileReq) {
        try{
            Long result = this.userProfileService.addProfile(principal, profileReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
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

    @ResponseBody
    @ApiOperation(value = "유저 프로필 세부 조회", notes ="Path variable로 상세 조회할 프로필 Idx 식별자 넣어주세요!")
    @GetMapping("/profileList/{profileIdx}")
    public BaseResponse<GetProfileRes> getUserProfile(Principal principal, @PathVariable("profileIdx") Long profileIdx) throws BaseException {
        try {
            GetProfileRes getUserProfile = userProfileService.getUserProfile(principal, profileIdx);
            return new BaseResponse<>(getUserProfile);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PostMapping("/email")
    @ApiOperation(value="인증 번호 이메일 발급", notes="네이버 메일 (@naver.com) 형식의 이메일만 메일 전송이 가능합니다. 인증 번호 유효 시간은 5분으로, 시간이 지나면 코드는 삭제됩니다!")
    public BaseResponse<String> authEmail(Principal principal, @RequestBody @Valid PostEmailReq request) throws Exception {
        String code = this.userService.sendSimpleMessage(principal, request.getEmail());
        log.info("인증 코드 : " + code);
        return new BaseResponse<>("인증번호 발급 이메일을 전송하였습니다.");
    }

    @ResponseBody
    @GetMapping("/emailConfirm")
    @ApiOperation(value="인증 번호 비교", notes="이메일로 발급받으신 인증번호를 RequestBody에 넣어서 String으로 인증번호 인증 성공/실패 여부 반환하도록 했습니다!")
    public BaseResponse<String> confirmEmail(Principal principal, @RequestBody GetEmailCodeReq code) throws BaseException {
        if (this.userService.confirmEmail(principal, code)){
            return new BaseResponse<>("인증번호 인증에 성공하였습니다.");
        } else {
            return new BaseResponse<>("인증번호 인증에 실패하였습니다.");
        }
    }
}
