package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags={"달리기 및 기록 관련 API"})
@RequestMapping("/record")
public class RunningRecordController {
    RunningRecordService runningRecordService;

    @Autowired
    public RunningRecordController(RunningRecordService runningRecordService){
        this.runningRecordService = runningRecordService;
    }
    @PostMapping("/start")
    @ApiOperation(value="달리기 시작", notes="profileIdx, 속한 clubIdx 요청" +
            "응답 받은 runningRecordIdx를 가지고 있다가 달리기 종료, 일시 정지, 다시 달리기 할 때 " +
            "보내 주셔야 합니다.")
    public BaseResponse<PostRunningInitRes> startRunning(@RequestBody PostRunningInitReq runningInitReq) {
        try{
            PostRunningInitRes result = runningRecordService.startRunning(runningInitReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
