package com.example.relayRun.record.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.record.dto.*;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.LocationRepository;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.util.RecordDataHandler;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class RunningRecordService {
    UserProfileRepository userProfileRepository;
    RunningRecordRepository runningRecordRepository;
    LocationRepository locationRepository;
    MemberStatusRepository memberStatusRepository;
    UserRepository userRepository;
    ClubRepository clubRepository;
    TimeTableRepository timeTableRepository;
    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository,
                                LocationRepository locationRepository,
                                MemberStatusRepository memberStatusRepository,
                                TimeTableRepository timeTableRepository,
                                UserProfileRepository userProfileRepository,
                                UserRepository userRepository,
                                ClubRepository clubRepository) {
        this.runningRecordRepository = runningRecordRepository;
        this.locationRepository = locationRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.clubRepository = clubRepository;
        this.timeTableRepository = timeTableRepository;
    }

    /**
     * 달리기 시작 POST
     * @param runningInitReq
     * @return
     * @throws BaseException
     */
    public PostRunningInitRes startRunning(Principal principal, PostRunningInitReq runningInitReq) throws BaseException {
        try{
            Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(
                    runningInitReq.getProfileIdx(),
                    "ACCEPTED"
            );
            if (optionalMemberStatus.isEmpty()){
                throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
            }
            Optional<UserProfileEntity> optionalUserProfile = userProfileRepository.findById(runningInitReq.getProfileIdx());
            if (optionalUserProfile.isEmpty())
                throw new BaseException(BaseResponseStatus.POST_RECORD_NO_PROFILE_IDX);
            UserProfileEntity userProfileParam = optionalUserProfile.get();
            Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getName());
            if (optionalUser.isEmpty())
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            UserEntity userEntityPrincipal = optionalUser.get();
            if (!userEntityPrincipal.getUserIdx().equals(userProfileParam.getUserIdx().getUserIdx()))
                throw new BaseException(BaseResponseStatus.POST_RECORD_NOT_MATCH_PARAM_PRINCIPAL);
            MemberStatusEntity memberStatus = optionalMemberStatus.get();
            RunningRecordEntity recordEntity = new RunningRecordEntity();
            recordEntity.setMemberStatusIdx(memberStatus);
            recordEntity.setDistance(0.0f);
            recordEntity.setTime(0.0f);
            recordEntity.setPace(0.0f);
            recordEntity = runningRecordRepository.save(recordEntity);
            PostRunningInitRes result = new PostRunningInitRes();
            result.setRunningRecordIdx(recordEntity.getRunningRecordIdx());
            return result;
        }catch(NullPointerException e){
            throw new BaseException(BaseResponseStatus.EMPTY_TOKEN);
        }
    }

    public PostRunningFinishRes finishRunning(Principal principal, PostRunningFinishReq runningFinishReq) throws BaseException {
        try {
            Optional<RunningRecordEntity> oldOptionalRecord = runningRecordRepository.findById(runningFinishReq.getRunningRecordIdx());
            if (oldOptionalRecord.isEmpty()) {
                throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_RECORD_ID);
            }
            RunningRecordEntity oldRecord = oldOptionalRecord.get();
            Optional<UserEntity> optionalUserPrincipal = userRepository.findByEmail(principal.getName());
            if (optionalUserPrincipal.isEmpty())
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            UserEntity userPrincipal = optionalUserPrincipal.get();
            if (!oldRecord.getMemberStatusIdx().getUserProfileIdx().getUserIdx().equals(userPrincipal))
                throw new BaseException(BaseResponseStatus.POST_RECORD_NOT_MATCH_PARAM_PRINCIPAL);
            List<LocationEntity> locations = RecordDataHandler.toEntityList(runningFinishReq.getLocations());
            LocalTime timeFormat = runningFinishReq.getTime();
            Optional<TimeTableEntity> optionalTimeTable = timeTableRepository
                    .findByMemberStatusIdxAndDayAndStartLessThanEqualAndEndGreaterThanEqual(
                    oldRecord.getMemberStatusIdx(),
                    RecordDataHandler.toIntDay(locations.get(0).getTime().getDayOfWeek()),
                    locations.get(0).getTime().toLocalTime(),
                    locations.get(locations.size() - 1).getTime().toLocalTime()
            );
            if (optionalTimeTable.isEmpty())
                throw new BaseException(BaseResponseStatus.POST_RECORD_NO_TIMETABLE);
            // calculate success, running time
            TimeTableEntity timeTable = optionalTimeTable.get();
            if (oldRecord.getRunningStatus().equals("finish")) {
                throw new BaseException(BaseResponseStatus.POST_RECORD_ALREADY_FINISH);
            }
            Float seconds = RecordDataHandler.toSecond(timeFormat);
            String isSuccess =  RecordDataHandler.isSuccess(
                    timeTable.getGoalType(),
                    timeTable.getGoal(),
                    seconds,
                    runningFinishReq.getPace(),
                    runningFinishReq.getDistance()
            );
            // update entity
            oldRecord.setDistance(runningFinishReq.getDistance());
            oldRecord.setPace(runningFinishReq.getPace());
            oldRecord.setTime(seconds);
            oldRecord.setRunningStatus("finish");
            oldRecord.setGoalStatus(isSuccess);
            for (LocationEntity location : locations) {
                location.setRecordIdx(oldRecord);
            }
            locationRepository.saveAll(locations);
            runningRecordRepository.save(oldRecord);
            return new PostRunningFinishRes(isSuccess);
        } catch (ParseException e) {
            throw new BaseException(BaseResponseStatus.POST_PARSE_ERROR);
        } catch (NullPointerException e) {
            throw new BaseException(BaseResponseStatus.EMPTY_TOKEN);
        }
    }

    /**
     * 실시간 달리기 현황 GET
     * @param memberStatusIdx
     * @param startDate
     * @param endDate
     * @return
     * @throws BaseException
     */
    public List<GetRecordWithoutLocationRes> getRecordWithoutLocation(Long memberStatusIdx, LocalDateTime startDate, LocalDateTime endDate) throws BaseException {
        try {
            List<RunningRecordEntity> recordList = runningRecordRepository.selectByMemberStatusIdxAndDate(memberStatusIdx, startDate, endDate);
            if (recordList.isEmpty()) {
                return null;
            }
            List<GetRecordWithoutLocationRes> res = new ArrayList<>();
            for(RunningRecordEntity record : recordList) {
                res.add(
                        GetRecordWithoutLocationRes.builder()
                                .recordIdx(record.getRunningRecordIdx())
                                .date(record.getCreatedAt())
                                .runningStatus(record.getRunningStatus())
                                .build()
                );
            }
            return res;
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    /**
     * 기록 세부 조회 GET
     * @param idx
     * @return
     * @throws BaseException
     */
    public GetRecordByIdxRes getRecordByIdx(Long idx) throws BaseException {
        try {
            Optional<RunningRecordEntity> record = runningRecordRepository.findByRunningRecordIdxAndStatus(idx, "active");
            if (record.isEmpty()) {
                throw new Exception("RECORD_UNAVAILABLE");
            }

//            List<GetLocationRes> locationList = locationRepository.findByRecordIdx_RunningRecordIdx(idx);
            List<LocationEntity> getLocations = record.get().getLocations();

            List<GetLocationRes> locationList = new ArrayList<>();
            for (LocationEntity location : getLocations) {
                locationList.add(
                        GetLocationRes.builder()
                                .time(location.getTime())
                                .longitude((float) location.getPosition().getX())
                                .latitude((float) location.getPosition().getY())
                                .status(location.getStatus())
                                .build()
                );
            }

            return GetRecordByIdxRes.builder()
                    .recordIdx(idx)
                    .date(record.get().getCreatedAt())
                    .time(record.get().getTime())
                    .distance(record.get().getDistance())
                    .pace(record.get().getPace())
                    .goalStatus(record.get().getGoalStatus())
                    .locationList(locationList)
                    .build();

        } catch (Exception e) {
            if (e.getMessage().equals("RECORD_UNAVAILABLE")) {
                throw new BaseException(BaseResponseStatus.RECORD_UNAVAILABLE);
            } else {
                System.out.println("e = " + e);
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        }
    }

    /**
     * 개인 기록 일별 조회 GET
     * @param principal
     * @param date
     * @return
     * @throws BaseException
     */
    public GetDailyRes getDailyRecord(Principal principal, LocalDate date) throws BaseException {
        try {
            Optional<UserEntity> user = userRepository.findByEmail(principal.getName());

            List<RunningRecordEntity> records = new ArrayList<>();

            List<UserProfileEntity> profileList = userProfileRepository.findAllByUserIdx(user.get()).get();

            List<UserProfileEntity> profileList = userProfileRepository.findAllByUserIdxAndStatus(user.get(), "active");

            for (UserProfileEntity profile : profileList) {
                List<MemberStatusEntity> statusList = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndStatus(profile.getUserProfileIdx(), "active");
                for (MemberStatusEntity status : statusList) {
                    records.addAll(runningRecordRepository
                            .findByMemberStatusIdxAndCreatedAtBetweenAndRunningStatus(status, date.atStartOfDay(), date.plusDays(1).atStartOfDay(), "finish"));
                }
            }

            return RecordDataHandler.get_summary(records, date);

        } catch (NullPointerException e) { // principal이 없거나 맞지 않을 때
            throw new BaseException(BaseResponseStatus.EMPTY_TOKEN);

        } catch (DateTimeParseException e) { // 날짜 형식이 잘못됐을 때
            throw new BaseException(BaseResponseStatus.INVALID_DATE_FORMAT);

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    /**
     * 그룹 기록 일별 조회 GET
     * @param clubIdx
     * @param date
     * @return
     */
    public GetDailyRes getDailyGroup(Long clubIdx, LocalDate date) throws BaseException {
        try {
            Optional<ClubEntity> club = clubRepository.findByClubIdxAndStatus(clubIdx, "active");
            if (club.isEmpty()) {
                throw new Exception("CLUB_UNAVAILABLE");
            }
            List<RunningRecordEntity> records = runningRecordRepository
                    .findByMemberStatusIdx_ClubIdxAndCreatedAtBetweenAndRunningStatus(club.get(), date.atStartOfDay(), date.plusDays(1).atStartOfDay(), "finish");

            return RecordDataHandler.get_summary(records, date);
        } catch (Exception e) {
            if (e.getMessage().equals("CLUB_UNAVAILABLE")) {
                throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
            } else {
                System.out.println("e = " + e);
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        }
    }

    public List<MemberStatusEntity> getApplyList(UserEntity user) {
        List<MemberStatusEntity> applyList = new ArrayList<>();

        List<UserProfileEntity> profileList = userProfileRepository.findAllByUserIdxAndStatus(user, "active"); // 프로필 여러개
        for (UserProfileEntity profile : profileList) {
            List<MemberStatusEntity> statusList = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndStatus(profile.getUserProfileIdx(), "active");
            applyList.addAll(statusList);
        }
        return applyList;
}

    /**
     * 개인 기록 캘린더
     * @param principal
     * @param year
     * @param month
     * @return
     * @throws BaseException
     */
    public List<GetDailyRes> getCalender(Principal principal, Integer year, Integer month) throws BaseException {
        try {
            // principal에서 user 가져오기
            Optional<UserEntity> user = userRepository.findByEmail(principal.getName());

            if (user.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }

            // user의 모든 memberStatus 가져오기
            List<MemberStatusEntity> applyList = getApplyList(user.get());

            // memberStatus에 해당하는 기록 중 해당 월만 갖고오기
            List<RunningRecordEntity> recordList = runningRecordRepository.selectByMemberStatusAndYearAndMonthAndStatus(applyList, year, month, "active");

            // GetDailyRes로 변환
            List<GetDailyRes> calender = new ArrayList<>();
            for (RunningRecordEntity record : recordList) {
                calender.add(
                        GetDailyRes.builder()
                                .date(record.getCreatedAt().toLocalDate())
                                .totalTime(record.getTime())
                                .totalDist(record.getDistance())
                                .avgPace(record.getPace())
                                .build()
                );
            }

            return calender;

        } catch (NullPointerException e) { // principal이 없거나 맞지 않을 때
            throw new BaseException(BaseResponseStatus.EMPTY_TOKEN);

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
