package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.GetTimeTableListRes;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.dto.TimeTableDTO;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                               UserRepository userRepository,
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
            //신청 유저 정보
            Long userProfileIdx = memberStatus.getUserProfileIdx();
            Optional<UserProfileEntity> userProfile = userProfileRepository.findByUserProfileIdx(userProfileIdx);

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

            //시간표 등록
            List<TimeTableDTO> timeTables = memberStatus.getTimeTables();
            //1. formatter 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

            for (int i = 0; i < timeTables.size(); i++) {
                //2. 입력으로 들어온 string -> local date time으로 변환
                String startStr = timeTables.get(i).getStart();
                String endStr = timeTables.get(i).getEnd();
                LocalTime startTime = LocalTime.parse(startStr, formatter);
                LocalTime endTime = LocalTime.parse(endStr, formatter);

                //3. 중복 시간표 비교
                List<Long> duplicateTimeTableList = timeTableRepository.selectDuplicateTimeTable(clubIdx,
                        timeTables.get(i).getDay(), startTime, endTime);

                if(duplicateTimeTableList.size() > 0) {
                    throw new BaseException(BaseResponseStatus.POST_TIMETABLE_FAIL);
                }

                TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                        .memberStatusIdx(memberStatusEntity)
                        .day(timeTables.get(i).getDay())
                        .start(startTime)
                        .end(endTime)
                        .goal(timeTables.get(i).getGoal())
                        .goalType(timeTables.get(i).getGoalType())
                        .build();

                timeTableRepository.save(timeTableEntity);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.POST_MEMBER_STATUS_FAIL);
        }
    }

    @Transactional
    public List<GetTimeTableListRes> getTimeTablesByClubIdx(Long clubIdx) throws BaseException {
        try {
            //1. clubIdx로 memberStatus 조회
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByClubIdx_ClubIdx(clubIdx);

            List<GetTimeTableListRes> timeTableList = new ArrayList<>();

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

}
