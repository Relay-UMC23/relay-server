package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.dto.PostClubReq;
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

    // 그룹 생성
    public void makesClub(Principal principal, Long userProfileIdx, PostClubReq club) throws BaseException {
        // 로그인한 유저 userIdx 가져오기
        Optional<UserEntity> userEntity = this.userRepository.findByEmail(principal.getName());
        UserEntity userIdx = userEntity.get();
        // principal의 userIdx랑 userProfileIdx의 userIdx 가 같다면
        Optional<UserProfileEntity> findUserEntity = userProfileRepository.findUserIdxByUserProfileIdx(userProfileIdx);
        UserEntity findUserIdx = findUserEntity.get().getUserIdx();

            Optional<UserProfileEntity> userProfile = userProfileRepository.findByUserProfileIdx(userProfileIdx);
        try {    if (userIdx.equals(findUserIdx)) {
                ClubEntity clubEntity = ClubEntity.builder()
                        .name(club.getName())
                        .content(club.getContent())
                        .hostIdx(userProfile.get())
                        .level(club.getLevel())
                        .goalType(GoalType.NOGOAL)
                        .goal(club.getGoal())
                        .build();
                clubRepository.save(clubEntity);
            }
            else {
                throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
            }
        } catch (Exception e){
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.POST_CLUBS_FAIL);
        }
    }
}
