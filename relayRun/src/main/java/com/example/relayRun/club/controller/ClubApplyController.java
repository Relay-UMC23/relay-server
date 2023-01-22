package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/clubs/apply")
public class ClubApplyController {

    private ClubService clubService;

    public ClubApplyController(ClubService clubService) {
        this.clubService = clubService;
    }


    // 그룹 생성
    @ResponseBody
    @PostMapping("/{userProfileIdx}/generates")
    @ApiOperation(value="그룹 생성(방장)", notes="path variable에는 방장 idx, body에는 이름, 소개, 이미지, 최대인원, 레벨, 목표 분류(선택), 목표치 입력")
    public BaseResponse<String> getClubs(Principal principal, @PathVariable("userProfileIdx") Long userProfileIdx, @RequestBody PostClubReq club) {
        try {
            clubService.makesClub(principal, userProfileIdx, club);
            return new BaseResponse<>("그룹 생성을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
