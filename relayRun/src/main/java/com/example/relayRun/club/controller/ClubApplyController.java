package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
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
    @PostMapping("/generates")
    public BaseResponse<String> getClubs(Principal principal, @RequestBody PostClubReq club) {
        try {
            clubService.makesClub(principal, club);
            return new BaseResponse<>("그룹 생성을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
