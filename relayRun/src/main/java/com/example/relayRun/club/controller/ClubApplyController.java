package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Api(tags = {"그룹 생성/관리 관련 API"})
@RequestMapping(value = "/clubs/apply")
public class ClubApplyController {

    private ClubService clubService;

    public ClubApplyController(ClubService clubService) {
        this.clubService = clubService;
    }


    // 그룹 생성
    @ResponseBody
    @PostMapping("/{userProfileIdx}/generates")
    @ApiResponses({
            @ApiResponse(code = 200, message = "그룹 생성 완료"),
            @ApiResponse(code = 401, message = "권한을 찾을 수 없습니다"),
            @ApiResponse(code = 404, message = "서버 문제 발생"),
            @ApiResponse(code = 500, message = "페이지를 찾을 수 없습니다")
    })
    @ApiOperation(value="그룹 생성(방장)", notes="path variable에는 방장 idx, body에는 이름, 소개, 이미지, 최대인원, 레벨, 목표 분류(선택), 목표치 입력\n" +
            "hostIdx에는 그룹을 생성하려는 유저의 프로필 식별자값 (int)를 넣어주시면 됩니다!!\n" +
            "timetable 예시는 디스코드에 적어두었습니다!")
    public BaseResponse<String> getClubs(Principal principal, @ApiParam(value = "유저 프로필 식별자값") @PathVariable("userProfileIdx") Long userProfileIdx,
                                         @ApiParam(value = "그룹 생성에 필요한 request body 정보")@RequestBody PostClubReq club) {
        try {
            clubService.makesClub(principal, userProfileIdx, club);
            return new BaseResponse<>("그룹 생성을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
