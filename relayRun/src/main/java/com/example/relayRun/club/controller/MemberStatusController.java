package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetTimeTableAndUserProfileRes;
import com.example.relayRun.club.dto.GetTimeTableListRes;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.dto.PostTimeTableReq;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = {"그룹 지원 관련 API"})
@RequestMapping(value = "/clubs/member-status")
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    public MemberStatusController(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    @ApiOperation(value = "그룹 신청", notes = "path variable로 신청하고자 하는 그룹의 clubIdx, body로는 신청자의 userProfileIdx를 보내면 생성된 memberStatusIdx가 반환됩니다.")
    @ResponseBody
    @PostMapping("/{clubIdx}")
    public BaseResponse<Long> createMemberStatus(@ApiParam(value = "신청하고자 하는 그룹의 clubIdx") @PathVariable Long clubIdx, @ApiParam(value = "신청자의 userProfileIdx") @RequestBody PostMemberStatusReq memberStatus) {
        try {
            Long memberStatusIdx = memberStatusService.createMemberStatus(clubIdx, memberStatus);
            return new BaseResponse<>(memberStatusIdx);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value = "시간표 등록", notes = "path variable로 직전에 반환 받은 memberStatusIdx, body로는 신청자의 시간표 정보를 리스트 형식으로 보내면 시간표 등록이 완료됩니다.")
    @ResponseBody
    @PostMapping("/{memberStatusIdx}/time-tables")
    public BaseResponse<String> createTimeTable(@ApiParam(value = "직전에 반환 받은 memberStatusIdx") @PathVariable Long memberStatusIdx, @ApiParam(value = "신청자의 시간표 정보(리스트 형식)") @Valid @RequestBody PostTimeTableReq postTimeTableReq) {
        try {
            memberStatusService.createTimeTable(memberStatusIdx, postTimeTableReq);
            return new BaseResponse<>("시간표 등록 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value = "그룹의 전체 시간표 조회", notes = "path variable로 조회하고자 하는 그룹의 clubIdx를 보내면 해당 그룹의 전체 시간표를 리스트 형식으로 반환합니다.")
    @ResponseBody
    @GetMapping("/{clubIdx}/time-tables")
    public BaseResponse<List<GetTimeTableAndUserProfileRes>> getAllTimeTables(@ApiParam(value = "조회하고자 하는 그룹의 clubIdx")@PathVariable Long clubIdx) {
        try {
            List<GetTimeTableAndUserProfileRes> timeTableList = memberStatusService.getTimeTablesByClubIdx(clubIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // memberStatusIdx 별 시간표 반환 테스트
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetTimeTableListRes>> getTimeTablesByMemberStatusIdx(@RequestParam("mid") Long memberStatusIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getTimeTablesByMemberStatusIdx(memberStatusIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value = "개인 시간표 조회", notes = "path variable로 조회하고자 하는 유저의 userProfileIdx를 보내면 해당 유저의 시간표를 리스트 형식으로 반환합니다.")
    @ResponseBody
    @GetMapping("/time-tables/{userProfileIdx}")
    public BaseResponse<List<GetTimeTableListRes>> getUserTimeTable(@ApiParam(value = "조회하고자 하는 유저의 userProfileIdx")@PathVariable Long userProfileIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getUserTimeTable(userProfileIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ApiOperation(value = "시간표 수정", notes = "path variable로 수정하고자 하는 유저의 userProfileIdx, body로는 해당 유저의 시간표 정보를 리스트 형식으로 보내면 시간표 수정이 완료됩니다.")
    @ResponseBody
    @PostMapping("/time-tables/{userProfileIdx}")
    public BaseResponse<String> updateTimeTable(@ApiParam(value = "수정하고자 하는 유저의 userProfileIdx") @PathVariable Long userProfileIdx, @ApiParam(value = "유저의 시간표 정보(리스트 형식)") @Valid @RequestBody PostTimeTableReq postTimeTableReq) {
        try {
            memberStatusService.updateTimeTable(userProfileIdx, postTimeTableReq);
            return new BaseResponse<>("시간표 수정 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
