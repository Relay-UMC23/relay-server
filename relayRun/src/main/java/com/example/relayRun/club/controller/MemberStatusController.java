package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetTimeTableListRes;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "그룹 신청하기", notes = "clubIdx에는 그룹 인덱스, body에는 시간표 목록과 신청자 프로필 idx를 입력하면 됩니다.")
    @ResponseBody
    @PostMapping("/{clubIdx}")
    public BaseResponse<String> createMemberStatus(@PathVariable Long clubIdx, @Valid @RequestBody PostMemberStatusReq memberStatus) {
        try {
            memberStatusService.createMemberStatus(clubIdx, memberStatus);
            return new BaseResponse<>("그룹 신청 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation("그룹 시간표 조회하기")
    @ResponseBody
    @GetMapping("/{clubIdx}")
    public BaseResponse<List<GetTimeTableListRes>> getTimeTables(@PathVariable Long clubIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getTimeTables(clubIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
