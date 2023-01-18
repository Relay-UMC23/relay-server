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

<<<<<<< HEAD
import java.security.Principal;
=======
import java.util.ArrayList;
>>>>>>> fa8fdca78e3f58b8b77819092bf35c5d0febcd06
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private ClubRepository clubRepository;
    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

<<<<<<< HEAD
    public List<GetClubListRes> getClubs() throws BaseException {
        try {
            List<GetClubListRes> clubList = clubRepository.findByOrderByRecruitStatusDesc();
=======
    public List<ClubDTO.ClubList> getClubs() throws BaseException {
        try {
            List<ClubEntity> clubs = clubRepository.findAll();
            List<ClubDTO.ClubList> clubList = new ArrayList<>();

            for (ClubEntity c : clubs) {
                ClubDTO.ClubList club = new ClubDTO.ClubList();
                club.setClubIdx(c.getClubIdx());
                club.setContent(c.getContent());
                club.setName(c.getName());
                club.setImgURL(c.getImgURL());
                club.setRecruitStatus(c.getRecruitStatus());
                clubList.add(club);
            }
>>>>>>> fa8fdca78e3f58b8b77819092bf35c5d0febcd06
            return clubList;

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    // 그룹 생성
    public void makesClub(Principal principal, PostClubReq club) throws BaseException {
        try {
            Optional<UserEntity> userEntity = userRepository.findByEmail(principal.getName());
            Long userIdx = userEntity.get().getUserIdx();
            Optional<UserProfileEntity> user = userProfileRepository.findByUserIdx(userIdx);
            ClubEntity clubEntity = ClubEntity.builder()
                    .name(club.getName())
                    .content(club.getContent())
                    .hostIdx(user.get())
                    .level(club.getLevel())
                    .goalType(GoalType.NOGOAL)
                    .goal(club.getGoal())
                    .build();
            clubRepository.save(clubEntity);
        } catch (Exception e){
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.POST_CLUBS_FAIL);
        }
    }
}
