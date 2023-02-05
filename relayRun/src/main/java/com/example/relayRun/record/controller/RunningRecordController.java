package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.*;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

import java.util.List;

@RestController
@Api(tags={"달리기 및 기록 관련 API"})
@RequestMapping("/record")
public class RunningRecordController {
    RunningRecordService runningRecordService;

    @Autowired
    public RunningRecordController(RunningRecordService runningRecordService){
        this.runningRecordService = runningRecordService;
    }

    // 달리기 시작
    @PostMapping("/start")
    @ApiOperation(value="달리기 시작", notes="Request Body: profileIdx" +
            "응답 받은 runningRecordIdx를 가지고 있다가 달리기 종료시 보내 주셔야 합니다." +
            "Response: runningRecordIdx, 현재 시간 기준 시간표 정보(시작 시간, 끝시간, 목표 타입, 목표량)")
    public BaseResponse<PostRunningInitRes> startRunning(Principal principal, @RequestBody PostRunningInitReq runningInitReq) {
        try{
            PostRunningInitRes result = runningRecordService.startRunning(principal, runningInitReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/finish")
    @ApiOperation(value="달리기 최종종료", notes="start 요청에서 응답받은 idx, " +
            "계산한 거리, 시간(\"yyyy-MM-dd HH:mm:ss\" 형식), 속력, " +
            "기록들 (위치와 그때 시간(\"HH:mm:ss\" 형식), 달리기 상태)을 받아서, " +
            "목표에 도달했는지 반환함 (y/n)" +
            "시간표에 맞지 않은 기록의 경우 실패로 처리" +
            "최종 달리기 종료 시 호출")
    public BaseResponse<PostRunningFinishRes> finishRunning(Principal principal, @RequestBody PostRunningFinishReq runningFinishReq) {
        try{
            PostRunningFinishRes result = runningRecordService.finishRunning(principal,runningFinishReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // profile idx와 오늘 날짜로 조회
    @ApiOperation(value="프로필 기록 날짜별 조회", notes="token과 query로 프로필 idx, date를 입력해주세요 ex) record/?idx=1&date=2023-01-26\n" +
            "token이 없거나 해당 유저가 아닐 때는 위치 list가 null로 반환됩니다!")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetRecordByIdxRes> getRecordWithoutLocation(
            Principal principal,
            @RequestParam("idx") Long profileIdx,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        try {
            GetRecordByIdxRes rec = runningRecordService.getRecordByDate(principal, profileIdx, date);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 기록 세부 조회
    @ApiOperation(value="기록 idx로 조회", notes="path variable에 조회할 기록의 idx를 입력해주세요")
    @GetMapping("/{idx}")
    public BaseResponse<GetRecordByIdxRes> getRecordByIdx(Principal principal, @PathVariable("idx") Long idx) {
        try {
            GetRecordByIdxRes rec = runningRecordService.getRecordByIdx(principal, idx);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 하루 기록 조회
    @ApiOperation(value="개인 기록 일별 요약", notes="bearer에 조회할 유저의 토큰, query에 조회 날짜를 입력해주세요 ex record/summary/?date=2023-01-26")
    @GetMapping("/summary")
    public BaseResponse<GetDailyRes> getDailyRecord(Principal principal,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            GetDailyRes daily = runningRecordService.getDailyRecord(principal, date);
            return new BaseResponse<>(daily);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 기록 일별 요약", notes="조회할 그룹 idx를 입력해주세요, query에 조회 날짜를 입력해주세요 ex record/daily/{clubIdx}/?date=2023-01-27")
    @GetMapping("/summary/{clubIdx}/club")
    public BaseResponse<GetDailyRes> getDailyGroup(@PathVariable("clubIdx") Long idx, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            GetDailyRes dailyGroup = runningRecordService.getDailyGroup(idx, date);
            return new BaseResponse<>(dailyGroup);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="해당되는 월의 날짜별 개인(프로필) 기록 모음", notes="path에 프로필 idx, query에 년과 월을 입력해주세요 ex record/calender/{profileidx}?year=2023&month=1")
    @GetMapping("/calender/{profileIdx}")
    public BaseResponse<List<GetCalender>> getCalender(@PathVariable Long profileIdx, @RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        try {
            List<GetCalender> calender = runningRecordService.getCalender(profileIdx, year, month);
            return new BaseResponse<>(calender);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="해당되는 월의 날짜별 그룹 기록 모음", notes=", 조회할 그룹 idx와, query에는 년과 월을 입력해주세요 ex record/calender/1/club?year=2023&month=1")
    @GetMapping("/calender/{clubIdx}/club")
    public BaseResponse<List<GetCalender>> getClubCalender(@PathVariable("clubIdx") Long idx, @RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        try {
            List<GetCalender> calender = runningRecordService.getClubCalender(idx, year, month);
            return new BaseResponse<>(calender);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }
}
