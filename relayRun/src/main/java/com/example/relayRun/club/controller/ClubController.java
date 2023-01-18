package com.example.relayRun.club.controller;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.club.dto.ClubDTO;
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

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<ClubDTO.ClubList>> getClubs(@RequestParam(required = false) String search) {
        try {
            List<ClubDTO.ClubList> clubList;
            if(search == null) {
                clubList = clubService.getClubs();
            } else {
                clubList = clubService.getClubsByName(search);
            }
            return new BaseResponse<>(clubList);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
