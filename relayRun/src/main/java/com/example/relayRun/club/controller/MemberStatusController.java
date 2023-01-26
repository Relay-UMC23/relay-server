package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetTimeTableListRes;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.dto.TimeTableDTO;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/clubs/clubs")
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    public MemberStatusController(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    @ResponseBody
    @PostMapping("/{clubIdx}")
    @ApiOperation(value = "그룹 신청 / 시간표 등록", notes = "path variable로 신청하고자 하는 그룹의 clubIdx, body로는 신청자의 userProfileIdx와 시간표 정보를 리스트 형식으로 보내면 그룹 신청이 완료됩니다.")
    public BaseResponse<String> createMemberStatus(@ApiParam(value = "신청하고자 하는 그룹의 clubIdx") @PathVariable Long clubIdx, @ApiParam(value = "신청자의 userProfileIdx와 시간표 정보(리스트 형식)") @Valid @RequestBody PostMemberStatusReq memberStatus) {
        try {
            memberStatusService.createMemberStatus(clubIdx, memberStatus);
            return new BaseResponse<>("그룹 신청 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{clubIdx}")
    @ApiOperation(value = "그룹의 전체 시간표 조회", notes = "path variable로 조회하고자 하는 그룹의 clubIdx를 보내면 해당 그룹의 전체 시간표를 리스트 형식으로 반환합니다.")
    public BaseResponse<List<GetTimeTableListRes>> getAllTimeTables(@ApiParam(value = "조회하고자 하는 그룹의 clubIdx")@PathVariable Long clubIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getTimeTables(clubIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/member-status/{userProfileIdx}")
    @ApiOperation(value = "개인 시간표 조회", notes = "path variable로 조회하고자 하는 유저의 userProfileIdx를 보내면 해당 유저의 시간표를 리스트 형식으로 반환합니다.")
    public BaseResponse<List<GetTimeTableListRes>> getUserTimeTable(@ApiParam(value = "조회하고자 하는 유저의 userProfileIdx")@PathVariable Long userProfileIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getUserTimeTable(userProfileIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    @ResponseBody
//    @PatchMapping("/member-status/{userProfileIdx}")
//    @ApiOperation(value = "개인 시간표 수정", notes = "path variable로 수정하고자 하는 유저의 userProfileIdx, body로는 시간표 정보를 리스트 형식으로 보내면 시간표 수정이 완료됩니다.")
//    public BaseResponse<String> updateUserTimeTable(@ApiParam(value = "수정하고자 하는 유저의userProfileIdx") @PathVariable Long userProfileIdx, @ApiParam(value = "시간표 정보(리스트 형식)") @Valid @RequestBody List<TimeTableDTO> timeTables) {
//        try {
//            memberStatusService
//            return new BaseResponse<>("시간표 수정 완료");
//        } catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }
}
