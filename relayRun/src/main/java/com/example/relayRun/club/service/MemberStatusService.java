package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.*;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberStatusService {

    private final MemberStatusRepository memberStatusRepository;
    private final TimeTableRepository timeTableRepository;
    private final UserProfileRepository userProfileRepository;
    private final ClubRepository clubRepository;

    public MemberStatusService(MemberStatusRepository memberStatusRepository,
                               TimeTableRepository timeTableRepository,
                               UserProfileRepository userProfileRepository,
                               ClubRepository clubRepository) {
        this.memberStatusRepository = memberStatusRepository;
        this.timeTableRepository = timeTableRepository;
        this.userProfileRepository = userProfileRepository;
        this.clubRepository = clubRepository;
    }

    @Transactional
    public void createMemberStatus(Long clubIdx, PostMemberStatusReq memberStatus) throws BaseException {
        try {
            Long userProfileIdx = memberStatus.getUserProfileIdx();
            Optional<UserProfileEntity> userProfile = userProfileRepository.findByUserProfileIdx(userProfileIdx);
            if(userProfile.isEmpty()) {
                throw new BaseException(BaseResponseStatus.USER_PROFILE_EMPTY);
            }

            List<MemberStatusEntity> validationList = memberStatusRepository.findByUserProfileIdx_UserProfileIdx(userProfileIdx);
            if(!validationList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.DUPLICATE_MEMBER_STATUS);
            }

            //신청 대상 그룹 정보
            Optional<ClubEntity> club = clubRepository.findById(clubIdx);
            if(club.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
            }

            //member_status 등록
            MemberStatusEntity memberStatusEntity = MemberStatusEntity.builder()
                    .clubIdx(club.get())
                    .userProfileIdx(userProfile.get())
                    .build();

            memberStatusRepository.save(memberStatusEntity);

            Long memberStatusIdx = memberStatusEntity.getMemberStatusIdx();
            List<TimeTableDTO> timeTables = memberStatus.getTimeTables();

            this.createTimeTable(memberStatusIdx, timeTables);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_MEMBER_STATUS_FAIL);
        }
    }

    @Transactional
    public void createTimeTable(Long memberStatusIdx, List<TimeTableDTO> timeTables) throws BaseException {
        try {
            Optional<MemberStatusEntity> memberStatusEntity = memberStatusRepository.findById(memberStatusIdx);
            if(memberStatusEntity.isEmpty()) {
                throw new BaseException(BaseResponseStatus.INVALID_MEMBER_STATUS);
            }

            for (int i = 0; i < timeTables.size(); i++) {
                TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                        .memberStatusIdx(memberStatusEntity.get())
                        .day(timeTables.get(i).getDay())
                        .start(timeTables.get(i).getStart())
                        .end(timeTables.get(i).getEnd())
                        .goal(timeTables.get(i).getGoal())
                        .goalType(timeTables.get(i).getGoalType())
                        .build();

                timeTableRepository.save(timeTableEntity);
            }
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_TIME_TABLE_FAIL);
        }
    }

    @Transactional(readOnly = true)
    public List<GetTimeTableAndUserProfileRes> getTimeTablesByClubIdx(Long clubIdx) throws BaseException {
        try {
            //1. clubIdx로 memberStatus 조회
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByClubIdx_ClubIdx(clubIdx);
            if(memberStatusEntityList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.CLUB_EMPTY);
            }

            List<GetTimeTableListRes> timeTableList = new ArrayList<>();
            List<GetTimeTableAndUserProfileRes> allTimeTableList = new ArrayList<>();

            //2. 해당 memberStatusIdx로 TimeTable 조회
            for(MemberStatusEntity memberStatus : memberStatusEntityList) {
                List<TimeTableEntity> timeTableEntityList = timeTableRepository.findByMemberStatusIdx_MemberStatusIdx(memberStatus.getMemberStatusIdx());

                for(TimeTableEntity timeTableEntity : timeTableEntityList) {
                    GetTimeTableListRes timeTable = GetTimeTableListRes.builder()
                            .timeTableIdx(timeTableEntity.getTimeTableIdx())
                            .day(timeTableEntity.getDay())
                            .start(timeTableEntity.getStart())
                            .end(timeTableEntity.getEnd())
                            .goal(timeTableEntity.getGoal())
                            .goalType(timeTableEntity.getGoalType())
                            .build();

                    timeTableList.add(timeTable);
                }

                //유저 정보
                Long userProfileIdx = memberStatus.getUserProfileIdx().getUserProfileIdx();
                UserProfileEntity userProfileEntity = userProfileRepository.findOneByUserProfileIdx(userProfileIdx);

                GetTimeTableAndUserProfileRes allTimeTable = GetTimeTableAndUserProfileRes.builder()
                        .userProfileIdx(userProfileIdx)
                        .nickName(userProfileEntity.getNickName())
                        .timeTables(timeTableList)
                        .build();

                allTimeTableList.add(allTimeTable);
            }
            return allTimeTableList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<GetTimeTableListRes> getUserTimeTable(Long userProfileIdx) throws BaseException {
        try {
            //memberStatusIdx 찾기
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByUserProfileIdx_UserProfileIdx(userProfileIdx);
            if(memberStatusEntityList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.USER_PROFILE_EMPTY);
            }

            //memberStatusIdx로 시간표 조회
            Long memberStatusIdx = memberStatusEntityList.get(0).getMemberStatusIdx();
            List<TimeTableEntity> timeTableEntityList = timeTableRepository.findByMemberStatusIdx_MemberStatusIdx(memberStatusIdx);

            List<GetTimeTableListRes> timeTableList = new ArrayList<>();

            for(TimeTableEntity timeTableEntity : timeTableEntityList) {
                GetTimeTableListRes timeTable = GetTimeTableListRes.builder()
                        .timeTableIdx(timeTableEntity.getTimeTableIdx())
                        .day(timeTableEntity.getDay())
                        .start(timeTableEntity.getStart())
                        .end(timeTableEntity.getEnd())
                        .goal(timeTableEntity.getGoal())
                        .goalType(timeTableEntity.getGoalType())
                        .build();

                timeTableList.add(timeTable);
            }
            return timeTableList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public List<GetTimeTableListRes> getTimeTablesByMemberStatusIdx(Long memberStatusIdx) throws BaseException {
        try {
            List<TimeTableEntity> timeTableEntityList = timeTableRepository.findByMemberStatusIdx_MemberStatusIdx(memberStatusIdx);
            List<GetTimeTableListRes> timeTableList = new ArrayList<>();

            for(TimeTableEntity timeTableEntity : timeTableEntityList) {
                GetTimeTableListRes timeTable = GetTimeTableListRes.builder()
                        .timeTableIdx(timeTableEntity.getTimeTableIdx())
                        .day(timeTableEntity.getDay())
                        .start(timeTableEntity.getStart())
                        .end(timeTableEntity.getEnd())
                        .goal(timeTableEntity.getGoal())
                        .goalType(timeTableEntity.getGoalType())
                        .build();

                timeTableList.add(timeTable);
            }
            return timeTableList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public void updateTimeTable(Long userProfileIdx, PostTimeTableReq postTimeTableReq) throws BaseException {
        try {
            //memberStatusIdx 찾기
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByUserProfileIdx_UserProfileIdx(userProfileIdx);
            if(memberStatusEntityList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.USER_PROFILE_EMPTY);
            }

            //memberStatusIdx로 시간표 조회
            Long memberStatusIdx = memberStatusEntityList.get(0).getMemberStatusIdx();
            List<TimeTableEntity> timeTableEntityList = timeTableRepository.findByMemberStatusIdx_MemberStatusIdx(memberStatusIdx);

            for(TimeTableEntity timeTableEntity : timeTableEntityList) {
                timeTableRepository.delete(timeTableEntity);
            }

            List<TimeTableDTO> timeTables = postTimeTableReq.getTimeTables();

            for (int i = 0; i < timeTables.size(); i++) {
                TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                        .memberStatusIdx(memberStatusEntityList.get(0))
                        .day(timeTables.get(i).getDay())
                        .start(timeTables.get(i).getStart())
                        .end(timeTables.get(i).getEnd())
                        .goal(timeTables.get(i).getGoal())
                        .goalType(timeTables.get(i).getGoalType())
                        .build();

                timeTableRepository.save(timeTableEntity);
            }
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_TIME_TABLE_FAIL);
        }
    }

}
