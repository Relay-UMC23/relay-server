package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.GetClubDetailRes;
import com.example.relayRun.club.dto.GetMemberOfClubRes;
import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.dto.GetClubListRes;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final MemberStatusService memberStatusService;

    public ClubService(ClubRepository clubRepository, UserRepository userRepository, UserProfileRepository userProfileRepository,
                       MemberStatusRepository memberStatusRepository,
                       MemberStatusService memberStatusService) {

        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.memberStatusService = memberStatusService;
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

    public List<GetMemberOfClubRes> getMemberOfClub(Long clubIdx) throws BaseException {
        try {
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findAllByClubIdx_ClubIdxAndApplyStatus(clubIdx, "ACCEPTED");
            if (memberStatusEntityList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
            }
            List<GetMemberOfClubRes> res = new ArrayList<>();
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
                res.add(getMemberOfClubRes);
            }
            return res;
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetClubDetailRes getClubDetail(Long clubIdx) throws BaseException {
        try {
            Optional<ClubEntity> optional = clubRepository.findById(clubIdx);
            if (optional.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
            }
            ClubEntity clubEntity = optional.get();
            return GetClubDetailRes.builder()
                    .clubIdx(clubEntity.getClubIdx())
                    .imgURL(clubEntity.getImgURL())
                    .name(clubEntity.getName())
                    .content(clubEntity.getContent())
                    .hostIdx(clubEntity.getHostIdx().getUserProfileIdx())
                    .level(clubEntity.getLevel())
                    .goalType(clubEntity.getGoalType())
                    .goal(clubEntity.getGoal())
                    .build();
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
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

}