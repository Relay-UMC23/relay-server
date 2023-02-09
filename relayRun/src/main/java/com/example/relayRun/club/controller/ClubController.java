package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.*;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import io.swagger.annotations.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
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

    public ClubController(ClubService clubService,
                          MemberStatusService memberStatusService,
                          RunningRecordService runningRecordService) {
        this.clubService = clubService;
        this.memberStatusService = memberStatusService;
        this.runningRecordService = runningRecordService;
    }

    @ApiOperation(value="메인 페이지", notes="헤더로 access 토큰, path variable로 userProfileIdx를 보내주세요.")
    @ResponseBody
    @GetMapping("/home/{userProfileIdx}")
    public BaseResponse<List<GetMemberOfClubRes>> getHome(
            Principal principal,
            @ApiParam(value = "조회하고자 하는 유저의 userProfileIdx") @PathVariable Long userProfileIdx
    ) {
        try {
            Long clubIdx = clubService.getClubIdx(principal, userProfileIdx);
            List<GetMemberOfClubRes> getMemberOfClubResList = clubService.getMemberOfClub(clubIdx);
            for(GetMemberOfClubRes getMemberOfClubRes : getMemberOfClubResList) {
                LocalDate date = LocalDate.now();
                getMemberOfClubRes.setRunningRecord(runningRecordService.getRecordWithoutLocation(getMemberOfClubRes.getMemberStatusIdx(), date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
            }
            return new BaseResponse<>(getMemberOfClubResList);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 목록 조회(전체, 검색)", notes="URI 뒤에 search parameter로 그룹 이름을 검색할 수 있다. 아무것도 넘기지 않을 경우 전체 목록이 조회된다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetClubListRes>> getClubs(
            @ApiParam(value = "그룹 검색어") @RequestParam(required = false) String search) {
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

    @ApiOperation(value="그룹 생성(방장)", notes="token 필요 / body에는 이름, 방장 idx, 소개, 이미지, 최대인원, 레벨, 목표 분류(선택), 목표치 입력\n" +
            "hostIdx에는 그룹을 생성하려는 유저의 프로필 식별자값 (int)를 넣어주시면 됩니다!!\n" +
            "timetable 예시는 디스코드에 적어두었습니다!")
    @ApiResponses({
            @ApiResponse(code = 200, message = "그룹 생성 완료"),
            @ApiResponse(code = 401, message = "권한을 찾을 수 없습니다"),
            @ApiResponse(code = 404, message = "서버 문제 발생"),
            @ApiResponse(code = 500, message = "페이지를 찾을 수 없습니다")
    })
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> makeClub(
            Principal principal,
            @ApiParam(value = "그룹 생성에 필요한 request body 정보") @Valid @RequestBody PostClubReq clubReq,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
//                ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
//                String errMsg = objectError.getDefaultMessage();
                throw new BaseException(BaseResponseStatus.VALIDATION_ERROR);
            }
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

    @ApiOperation(value="그룹 삭제", notes="path variable로 그룹 아이디를 전달해주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}/deletion")
    public BaseResponse<String> deleteClub(Principal principal, @PathVariable Long clubIdx) {
        try {
            clubService.deleteClub(principal, clubIdx);
            return new BaseResponse<>("그룹이 삭제되었습니다.");
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹의 모집완료 전환", notes="모집 인원이 모두 다 차면 자동 모집완료 처리 해야합니다.")
    @ResponseBody
    @PatchMapping("/{clubIdx}/recruit-finished")
    public BaseResponse<String> patchRecruitFinished(@PathVariable("clubIdx") Long clubIdx){
        try {
            clubService.updateClubRecruitFinished(clubIdx);
            return new BaseResponse<>("그룹의 모집 상태를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹의 모집중 전환", notes="모집 인원이 모두 다 찬 상황에서 팀원이 나가면 자동 모집중 처리 해야합니다.")
    @ResponseBody
    @PatchMapping("/{clubIdx}/recruit-recruiting")
    public BaseResponse<String> patchRecruitRecruiting(@PathVariable("clubIdx") Long clubIdx){
        try {
            clubService.updateClubRecruitRecruiting(clubIdx);
            return new BaseResponse<>("그룹의 모집 상태를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 정보 변경 (방장만 가능)", notes="access 토큰, 그룹 식별자, 변경하고자 하는 그룹 정보 값 전체를 넘겨주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}")
    public BaseResponse<String> patchClubInfo(
            Principal principal,
            @PathVariable("clubIdx") Long clubIdx,
            @Valid @RequestBody PatchClubInfoReq clubInfoReq,
            BindingResult bindingResult
    ){
        try {
            if (bindingResult.hasErrors()) {
                throw new BaseException(BaseResponseStatus.VALIDATION_ERROR);
            }
            clubService.updateClubInfo(principal, clubIdx, clubInfoReq);
            return new BaseResponse<>("그룹의 정보를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    
    @ApiOperation(value="그룹 방장 위임", notes="path variable로 클럽 아이디를 전달하고 현재 프로필 아이디, 방장이 될 유저의 프로필 아이디를 body로 전달해주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}/host-change")
    public BaseResponse<String> patchClubHost(Principal principal, @PathVariable("clubIdx") Long clubIdx, @Valid @RequestBody PatchHostReq hostReq) {
        try {
            clubService.updateClubHost(principal, clubIdx, hostReq);
            return new BaseResponse<>("방장을 위임 하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
