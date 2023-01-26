package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.GetDailyRes;
import com.example.relayRun.record.dto.GetRecordByIdxRes;
import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/record")
public class RunningRecordController {
    RunningRecordService runningRecordService;

    @Autowired
    public RunningRecordController(RunningRecordService runningRecordService){
        this.runningRecordService = runningRecordService;
    }

    // 달리기 시작
    @PostMapping("/start")
    @ApiOperation(value="달리기 시작", notes="profileIdx, 속한 clubIdx 요청")
    public BaseResponse<PostRunningInitRes> startRunning(@RequestBody PostRunningInitReq runningInitReq) {
        try{
            PostRunningInitRes result = runningRecordService.startRunning(runningInitReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
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
}
