package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.PatchClubRecruitStatusReq;
import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.util.GoalType;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;


    public ClubService(ClubRepository clubRepository, UserRepository userRepository, UserProfileRepository userProfileRepository) {

        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public List<GetClubListRes> getClubs() throws BaseException {
        try {
            List<GetClubListRes> clubList = clubRepository.findByOrderByRecruitStatusDesc();
            return clubList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetClubListRes> getClubsByName(String search) throws BaseException {
        try {
            List<GetClubListRes> clubList = clubRepository.findByNameContaining(search);
            return clubList;
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 그룹 생성
    public void makesClub(Principal principal, Long userProfileIdx, PostClubReq club) throws BaseException {
        // 로그인한 유저 userIdx 가져오기
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if(optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userIdx = optional.get();
        // principal의 userIdx랑 userProfileIdx의 userIdx 가 같다면
        Optional<UserProfileEntity> profileOptional = userProfileRepository.findByUserProfileIdx(userProfileIdx);
        if(profileOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }
        if(club.getName().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_NAME_EMPTY);
        }
        if(club.getContent().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_CONTENTS_EMPTY);
        }
        if(club.getLevel()==null || club.getLevel().toString().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_LEVEL_EMPTY);
        }
        UserProfileEntity userProfile = profileOptional.get();
            if (userIdx.getUserIdx().equals(userProfile.getUserIdx().getUserIdx())) {
                if(club.getGoalType() == null || club.getGoalType().toString().isEmpty()) {
                    ClubEntity clubEntity = ClubEntity.builder()
                            .name(club.getName())
                            .content(club.getContent())
                            .imgURL(club.getImgURL())
                            .hostIdx(userProfile)
                            .maxNum(club.getMaxNum())
                            .level(club.getLevel())
                            .goalType(GoalType.NOGOAL)
                            .goal(club.getGoal())
                            .build();
                    clubRepository.save(clubEntity);
                }
                else{
                    ClubEntity clubEntity = ClubEntity.builder()
                            .name(club.getName())
                            .content(club.getContent())
                            .imgURL(club.getImgURL())
                            .hostIdx(userProfile)
                            .maxNum(club.getMaxNum())
                            .level(club.getLevel())
                            .goalType(club.getGoalType())
                            .goal(club.getGoal())
                            .build();
                    clubRepository.save(clubEntity);
                }

            }
            else {
                throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EQUALS);
            }
    }

    public void changeClubRecruitStatus(Long clubIdx, PatchClubRecruitStatusReq clubRecruitStatusReq) throws BaseException {
        Optional<ClubEntity> optional = clubRepository.findByClubIdx(clubIdx);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.PATCH_CLUB_ID_WRONG);
        }
        ClubEntity clubEntity = optional.get();
        UserProfileEntity hostUserProfileEntity = clubEntity.getHostIdx();
        if(clubRecruitStatusReq.getUserProfileIdx().equals(hostUserProfileEntity.getUserProfileIdx())) {
            clubEntity.changeRecruitStatus(clubRecruitStatusReq.getRecruitStatus());
        } else {
            throw new BaseException(BaseResponseStatus.PATCH_NOT_HOST);
        }
    }
}
