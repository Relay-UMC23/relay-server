package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/clubs")
public class ClubController {

    private ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    // 그룹 조회
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetClubListRes>> getClubs() {
        try {
            List<GetClubListRes> clubList = clubService.getClubs();
            return new BaseResponse<>(clubList);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
