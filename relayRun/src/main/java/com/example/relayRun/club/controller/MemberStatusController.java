package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.*;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Api(tags = {"그룹 지원 관련 API"})
@RequestMapping(value = "/clubs/member-status")
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    public MemberStatusController(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    @ApiOperation(value = "그룹 신청", notes = "path variable로 신청하고자 하는 그룹의 clubIdx, body로는 신청자의 userProfileIdx와 신청자의 시간표 정보를 리스트 형식으로 보내면 그룹 신청과 시간표 등록이 완료됩니다.")
    @ResponseBody
    @PostMapping("/{clubIdx}")
    public BaseResponse<String> createMemberStatus(
            @ApiParam(value = "신청하고자 하는 그룹의 clubIdx") @PathVariable Long clubIdx,
            @ApiParam(value = "신청자의 userProfileIdx과 시간표 정보") @Valid @RequestBody PostMemberStatusReq memberStatus,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BaseException(BaseResponseStatus.VALIDATION_ERROR);
            }
            memberStatusService.createMemberStatus(clubIdx, memberStatus);
            return new BaseResponse<>("그룹 신청 및 시간표 등록 완료");
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
    public BaseResponse<List<GetTimeTableListRes>> getUserTimeTable(
            @ApiParam(value = "조회하고자 하는 유저의 userProfileIdx") @PathVariable Long userProfileIdx

    ) {
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
    public BaseResponse<String> updateTimeTable(
            @ApiParam(value = "수정하고자 하는 유저의 userProfileIdx") @PathVariable Long userProfileIdx,
            @ApiParam(value = "유저의 시간표 정보(리스트 형식)") @Valid @RequestBody PostTimeTableReq postTimeTableReq,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BaseException(BaseResponseStatus.VALIDATION_ERROR);
            }
            memberStatusService.updateTimeTable(userProfileIdx, postTimeTableReq);
            return new BaseResponse<>("시간표 수정 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 나가기", notes="헤더로 access 토큰, path variable로 clubIdx, body로 userProfileIdx를 보내면 그룹 나가기가 완료됩니다.")
    @ResponseBody
    @PatchMapping("/{clubIdx}")
    public BaseResponse<String> leaveClub(
            Principal principal,
            @PathVariable Long clubIdx,
            @RequestBody PatchDeleteMemberReq patchDeleteMemberReq) {
        try {
            Long userProfileIdx = patchDeleteMemberReq.getUserProfileIdx();
            memberStatusService.updateMemberStatus(principal, clubIdx, userProfileIdx);
            return new BaseResponse<>("그룹 나가기 완료");
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="멤버 강퇴", notes="")
    @ResponseBody
    @PatchMapping("/{clubIdx}/members/deletion")
    public BaseResponse<String> deleteMember(
            Principal principal,
            @PathVariable Long clubIdx,
            @RequestBody PatchDeleteMemberReq patchDeleteMemberReq) {
        try {
            String userProfileName = memberStatusService.deleteClubMember(principal, clubIdx, patchDeleteMemberReq);
            return new BaseResponse<>(userProfileName + "이(가) 강퇴되었습니다.");
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
