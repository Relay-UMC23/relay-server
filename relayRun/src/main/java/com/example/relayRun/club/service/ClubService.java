package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.*;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.dto.GetMemberProfileRes;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.record.service.RunningRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final MemberStatusService memberStatusService;

    private final RunningRecordService runningRecordService;

    public ClubService(ClubRepository clubRepository, UserRepository userRepository, UserProfileRepository userProfileRepository,
                       MemberStatusRepository memberStatusRepository,
                       MemberStatusService memberStatusService, RunningRecordService runningRecordService) {

        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.memberStatusService = memberStatusService;
        this.runningRecordService = runningRecordService;
    }

    public Long getClubIdx(Principal principal, Long userProfileIdx) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if (optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();

        Optional<UserProfileEntity> optionalUserProfileEntity = userProfileRepository.findByUserProfileIdx(userProfileIdx);
        if (optionalUserProfileEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.USER_PROFILE_EMPTY);
        }
        UserProfileEntity userProfileEntity = optionalUserProfileEntity.get();

        if(!userProfileEntity.getUserIdx().equals(userEntity)) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_FIND_USER);
        }

        Optional<MemberStatusEntity> memberStatusEntity = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndApplyStatusAndStatus(userProfileIdx, "ACCEPTED", "active");
        if(memberStatusEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER_STATUS);
        }

        return memberStatusEntity.get().getClubIdx().getClubIdx();
    }

    public List<GetClubListRes> getClubs() throws BaseException {
        try {
            return clubRepository.findByOrderByRecruitStatusDesc();
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetClubListRes> getClubsByName(String search) throws BaseException {
        try {
            return clubRepository.findByNameContaining(search);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public List<GetMemberOfClubRes> getRecordOfMembers(List<GetMemberOfClubRes> getMemberOfClubResList, String date) throws BaseException {
        for(GetMemberOfClubRes getMemberOfClubRes : getMemberOfClubResList) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", formatter);
            LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", formatter);
            getMemberOfClubRes.setRunningRecord(runningRecordService.getRecordWithoutLocation(getMemberOfClubRes.getMemberStatusIdx(), startDate, endDate));
        }
        return getMemberOfClubResList;
    }

    @Transactional
    public List<GetMemberOfClubRes> getMemberOfClub(Long clubIdx, String date) throws BaseException {
        List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findAllByClubIdx_ClubIdxAndApplyStatusAndStatus(clubIdx, "ACCEPTED", "active");
        if (memberStatusEntityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
        List<GetMemberOfClubRes> getMemberOfClubList = new ArrayList<>();
        for (MemberStatusEntity memberStatusEntity : memberStatusEntityList) {
            UserProfileEntity userProfileEntity = memberStatusEntity.getUserProfileIdx();
            GetMemberProfileRes getMemberProfileRes = GetMemberProfileRes.builder()
                    .userProfileIdx(userProfileEntity.getUserProfileIdx())
                    .nickname(userProfileEntity.getNickName())
                    .statusMsg(userProfileEntity.getStatusMsg())
                    .imgUrl(userProfileEntity.getImgURL())
                    .build();
            GetMemberOfClubRes getMemberOfClubRes = GetMemberOfClubRes.builder()
                    .memberStatusIdx(memberStatusEntity.getMemberStatusIdx())
                    .userProfile(getMemberProfileRes)
                    .build();
            getMemberOfClubList.add(getMemberOfClubRes);
        }

        return getRecordOfMembers(getMemberOfClubList, date);
    }

    @Transactional
    public GetClubDetailRes getClubDetail(Long clubIdx, String date) throws BaseException {
        Optional<ClubEntity> optional = clubRepository.findByClubIdxAndStatus(clubIdx, "active");
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
        }
        ClubEntity clubEntity = optional.get();

        List<GetMemberOfClubRes> getMemberOfClubResList = getMemberOfClub(clubIdx, date);

        return GetClubDetailRes.builder()
                .clubIdx(clubEntity.getClubIdx())
                .imgURL(clubEntity.getImgURL())
                .name(clubEntity.getName())
                .content(clubEntity.getContent())
                .hostIdx(clubEntity.getHostIdx().getUserProfileIdx())
                .level(clubEntity.getLevel())
                .goalType(clubEntity.getGoalType())
                .goal(clubEntity.getGoal())
                .getMemberOfClubResList(getMemberOfClubResList)
                .build();

    }

    // 그룹 생성
    @Transactional
    public void makesClub(Principal principal, PostClubReq clubReq) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if (optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();

        Optional<UserProfileEntity> optionalUserProfileEntity = userProfileRepository.findByUserProfileIdx(clubReq.getHostIdx());
        if (optionalUserProfileEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }
        UserProfileEntity userProfileEntity = optionalUserProfileEntity.get();

        if (!userProfileEntity.getUserIdx().equals(userEntity)) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EQUALS);
        }

        if (clubReq.getName().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_NAME_EMPTY);
        }
        if (clubReq.getContent().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_CONTENTS_EMPTY);
        }

        ClubEntity clubEntity = ClubEntity.builder()
                .name(clubReq.getName())
                .content(clubReq.getContent())
                .imgURL(clubReq.getImgURL())
                .hostIdx(userProfileEntity)
                .maxNum(clubReq.getMaxNum())
                .level(clubReq.getLevel())
                .goalType(clubReq.getGoalType())
                .goal(clubReq.getGoal())
                .build();
        clubRepository.save(clubEntity);

        // host Member Status update
        MemberStatusEntity memberStatusEntity = MemberStatusEntity.builder()
                .clubIdx(clubEntity)
                .userProfileIdx(optionalUserProfileEntity.get())
                .build();

        memberStatusRepository.save(memberStatusEntity);

        memberStatusService.createTimeTable(memberStatusEntity.getMemberStatusIdx(), clubReq.getTimeTable());
    }

    @Transactional
    public void deleteClub(Principal principal, Long clubIdx) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if (optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();

        Optional<ClubEntity> optionalClubEntity = clubRepository.findById(clubIdx);
        if (optionalClubEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
        }
        ClubEntity clubEntity = optionalClubEntity.get();

        if (clubEntity.getHostIdx().getUserIdx().equals(userEntity)) {
            clubEntity.setStatus("inactive");
            clubRepository.save(clubEntity);
        } else {
            throw new BaseException(BaseResponseStatus.PATCH_NOT_HOST);
        }
    }

    public void updateClubInfo(Principal principal, Long clubIdx, PatchClubInfoReq clubInfoReq) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();

        Optional<ClubEntity> optionalClubEntity = clubRepository.findByClubIdx(clubIdx);
        if (optionalClubEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
        }
        ClubEntity clubEntity = optionalClubEntity.get();
        if(clubEntity.getHostIdx().getUserIdx().equals(userEntity)) {
            clubEntity.setName(clubInfoReq.getName());
            clubEntity.setContent(clubInfoReq.getContent());
            clubEntity.setImgURL(clubInfoReq.getImgURL());
            clubEntity.setMaxNum(clubInfoReq.getMaxNum());
            clubEntity.setLevel(clubInfoReq.getLevel());
            clubEntity.setGoalType(clubInfoReq.getGoalType());
            clubEntity.setGoal(clubInfoReq.getGoal());
            clubEntity.setRecruitStatus(clubInfoReq.getRecruitStatus());

            clubRepository.save(clubEntity);
        } else {
            throw new BaseException(BaseResponseStatus.PATCH_NOT_HOST);
        }
    }

    public void updateClubRecruitFinished(Long clubIdx) throws BaseException {
        Optional<ClubEntity> optional = clubRepository.findByClubIdx(clubIdx);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
        }
        ClubEntity clubEntity = optional.get();
        clubEntity.setRecruitStatus("finished");
        clubRepository.save(clubEntity);
    }

    public void updateClubRecruitRecruiting(Long clubIdx) throws BaseException {
        Optional<ClubEntity> optional = clubRepository.findByClubIdx(clubIdx);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.CLUB_UNAVAILABLE);
        }
        ClubEntity clubEntity = optional.get();
        clubEntity.setRecruitStatus("recruiting");
        clubRepository.save(clubEntity);
    }
}