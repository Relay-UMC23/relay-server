package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.*;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @ApiOperation(value="달리기 시작", notes="profileIdx, 속한 clubIdx 요청" +
            "응답 받은 runningRecordIdx를 가지고 있다가 달리기 종료, 일시 정지, 다시 달리기 할 때 " +
            "보내 주셔야 합니다.")
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

    // member_status_idx와 오늘 날짜로 조회 테스트 API
    @ApiOperation(value="member_status_idx와 오늘 날짜로 조회", notes="Request Parameter : mid, date로 각 값을 입력해주세요")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRecordWithoutLocationRes>> getRecordWithoutLocation(@RequestParam("mid") Long memberStatusIdx, @RequestParam("date") String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", formatter);
            LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", formatter);
            List<GetRecordWithoutLocationRes> rec = runningRecordService.getRecordWithoutLocation(memberStatusIdx, startDate, endDate);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 기록 세부 조회
    @ApiOperation(value="기록 idx로 조회", notes="path variable에 조회할 기록의 idx를 입력해주세요")
    @GetMapping("/{idx}")
    public BaseResponse<GetRecordByIdxRes> getRecordByIdx(@PathVariable("idx") Long idx) {
        try {
            GetRecordByIdxRes rec = runningRecordService.getRecordByIdx(idx);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // 하루 기록 조회
    @ApiOperation(value="개인 기록 일별 요약", notes="bearer에 조회할 유저의 토큰, query에 조회 날짜를 입력해주세요 ex record/daily/?date=2023-01-26")
    @GetMapping("/daily")
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
    @GetMapping("/daily/{clubIdx}/club")
    public BaseResponse<GetDailyRes> getDailyGroup(@PathVariable("clubIdx") Long idx, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            GetDailyRes dailyGroup = runningRecordService.getDailyGroup(idx, date);
            return new BaseResponse<>(dailyGroup);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }
}
