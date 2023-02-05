package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.*;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Api(tags = {"그룹 관련 API"})
@RequestMapping(value = "/clubs")
public class ClubController {

    private final ClubService clubService;
    private final MemberStatusService memberStatusService;
    private final RunningRecordService runningRecordService;

    public ClubController(ClubService clubService, MemberStatusService memberStatusService, RunningRecordService runningRecordService) {
        this.clubService = clubService;
        this.memberStatusService = memberStatusService;
        this.runningRecordService = runningRecordService;
    }

    @ApiOperation(value="그룹 목록 조회(전체, 검색)", notes="URI 뒤에 search parameter로 그룹 이름을 검색할 수 있다. 아무것도 넘기지 않을 경우 전체 목록이 조회된다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetClubListRes>> getClubs(
            @ApiParam(value = "그룹 검색어")@RequestParam(required = false) String search
    ) {
        try {
            List<GetClubListRes> clubList;
            if(search == null) {
                clubList = clubService.getClubs();
            } else {
                clubList = clubService.getClubsByName(search);
            }
            return new BaseResponse<>(clubList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 그룹 생성
    @ResponseBody
    @PostMapping("")
    @ApiResponses({
            @ApiResponse(code = 200, message = "그룹 생성 완료"),
            @ApiResponse(code = 401, message = "권한을 찾을 수 없습니다"),
            @ApiResponse(code = 404, message = "서버 문제 발생"),
            @ApiResponse(code = 500, message = "페이지를 찾을 수 없습니다")
    })
    @ApiOperation(value="그룹 생성(방장)", notes="path variable에는 방장 idx, body에는 이름, 소개, 이미지, 최대인원, 레벨, 목표 분류(선택), 목표치 입력\n" +
            "hostIdx에는 그룹을 생성하려는 유저의 프로필 식별자값 (int)를 넣어주시면 됩니다!!\n" +
            "timetable 예시는 디스코드에 적어두었습니다!")
    public BaseResponse<String> makeClub(
            Principal principal,
            @ApiParam(value = "그룹 생성에 필요한 request body 정보")@RequestBody PostClubReq clubReq
    ) {
        try {
            clubService.makesClub(principal, clubReq);
            return new BaseResponse<>("그룹 생성을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 멤버 관련 정보 조회", notes="path variable로 그룹 아이디, query string으로 조회하고자 하는 날짜를 전달해주세요")
    @ResponseBody
    @GetMapping("/{clubIdx}/members")
    public BaseResponse<List<GetMemberOfClubRes>> getMemberOfClub(
            @ApiParam(value = "조회하고자 하는 그룹의 clubIdx")@PathVariable Long clubIdx,
            @ApiParam(value = "조회하고자 하는 날짜")@RequestParam("date") String date
    ) {
        try {
            List<GetMemberOfClubRes> getMemberOfClubResList = clubService.getMemberOfClub(clubIdx);
            for(GetMemberOfClubRes getMemberOfClubRes : getMemberOfClubResList) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", formatter);
                LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", formatter);
                getMemberOfClubRes.setRunningRecord(runningRecordService.getRecordWithoutLocation(getMemberOfClubRes.getMemberStatusIdx(), startDate, endDate));
            }
            return new BaseResponse<>(getMemberOfClubResList);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 페이지 정보 조회", notes="path variable로 그룹 아이디, query string으로 현재 날짜 전달해주세요")
    @ResponseBody
    @GetMapping("/{clubIdx}")
    public BaseResponse<GetClubDetailRes> getClubDetail(
            @ApiParam(value = "조회하고자 하는 그룹의 clubIdx")@PathVariable Long clubIdx,
            @ApiParam(value = "조회하고자 하는 날짜")@RequestParam("date") String date
    ) {
        try {
            GetClubDetailRes getClubDetailRes = clubService.getClubDetail(clubIdx);
            getClubDetailRes.setGetMemberOfClubResList(getMemberOfClub(clubIdx, date).getResult());
            return new BaseResponse<>(getClubDetailRes);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value = "그룹의 전체 시간표 조회", notes = "path variable로 조회하고자 하는 그룹의 clubIdx를 보내면 해당 그룹의 전체 시간표를 리스트 형식으로 반환합니다.")
    @ResponseBody
    @GetMapping("/{clubIdx}/time-tables")
    public BaseResponse<List<GetTimeTableAndUserProfileRes>> getAllTimeTables(
            @ApiParam(value = "조회하고자 하는 그룹의 clubIdx")@PathVariable Long clubIdx
    ) {
        try {
            List<GetTimeTableAndUserProfileRes> timeTableList = memberStatusService.getTimeTablesByClubIdx(clubIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
