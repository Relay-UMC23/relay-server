package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.*;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation(value="해당 프로필 일별 기록 조회", notes="위치 원할시 token 필요, query로는 프로필 idx, date를 입력해주세요 ex) record/?idx=1&date=2023-01-26\n" +
            "token이 없거나 해당 유저가 아닐 때는 위치 list가 null로 반환됩니다!")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetRecordByIdxRes> getRecordWithoutLocation(
            Principal principal,
            @ApiParam(value = "조회하고자 하는 유저 프로필 식별자")@RequestParam("idx") Long profileIdx,
            @ApiParam(value = "조회 날짜 | String | yyyy-MM-dd")@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        try {
            GetRecordByIdxRes rec = runningRecordService.getRecordByDate(principal, profileIdx, date);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 기록 세부 조회
    @ApiOperation(value="기록 idx로 조회 -> 임시, 날짜별 조회를 이용", notes="path variable에 조회할 기록의 idx를 입력해주세요")
    @GetMapping("/{recordIdx}")
    public BaseResponse<GetRecordByIdxRes> getRecordByIdx(
            Principal principal,
            @ApiParam(value = "조회하고자 하는 기록 식별자")@PathVariable("recordIdx") Long recordIdx
    ) {
        try {
            GetRecordByIdxRes rec = runningRecordService.getRecordByIdx(principal, recordIdx);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="개인 기록 일별 요약(요약이므로 세부 기록 조회를 위해서는 날짜별 조회 API 사용, 프로필이 많아지는 경우에만 사용)", notes="bearer에 조회할 유저의 토큰, query에 조회 날짜를 입력해주세요 ex record/summary/?date=2023-01-26")
    @GetMapping("/summary")
    public BaseResponse<GetDailyRes> getDailyRecord(
            Principal principal,
            @ApiParam(value = "조회 날짜 | String | yyyy-MM-dd")@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            GetDailyRes daily = runningRecordService.getDailyRecord(principal, date);
            return new BaseResponse<>(daily);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="해당 그룹 일별 기록 조회", notes="조회할 그룹 idx를 입력해주세요, query에 조회 날짜를 입력해주세요 ex record/summary/club/?clubIdx=1&date=2023-01-27")
    @GetMapping("/summary/club")
    public BaseResponse<GetDailyRes> getDailyGroup(
            @ApiParam(value = "조회하고자 하는 그룹의 식별자")@RequestParam("clubIdx") Long clubIdx,
            @ApiParam(value = "조회 날짜 | String | yyyy-MM-dd")@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        try {
            GetDailyRes dailyGroup = runningRecordService.getDailyGroup(clubIdx, date);
            return new BaseResponse<>(dailyGroup);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="해당 프로필의 월별 기록 조회", notes="조회할 프로필 idx, 년과 월을 입력해주세요 ex record/calender/?profileIdx=1&year=2023&month=1")
    @GetMapping("/calender")
    public BaseResponse<List<GetCalender>> getCalender(
            @ApiParam(value = "조회하고자 하는 유저의 프로필 식별자")@RequestParam("profileIdx") Long profileIdx,
            @ApiParam(value = "년 | Integer")@RequestParam("year") Integer year,
            @ApiParam(value = "월 | Integer")@RequestParam("month") Integer month
    ) {
        try {
            List<GetCalender> calender = runningRecordService.getCalender(profileIdx, year, month);
            return new BaseResponse<>(calender);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="해당 그룹의 월별 기록 조회", notes="조회할 그룹 idx, 년과 월을 입력해주세요 ex record/calender/club/?clubIdx=1&year=2023&month=1")
    @GetMapping("/calender/club")
    public BaseResponse<List<GetCalender>> getClubCalender(
            @ApiParam(value = "조회하고자 하는 그룹의 식별자")@RequestParam("clubIdx") Long clubIdx,
            @ApiParam(value = "년 | Integer")@RequestParam("year") Integer year,
            @ApiParam(value = "월 | Integer")@RequestParam("month") Integer month
    ) {
        try {
            List<GetCalender> calender = runningRecordService.getClubCalender(clubIdx, year, month);
            return new BaseResponse<>(calender);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }
}
