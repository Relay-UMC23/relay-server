package com.example.relayRun.user.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.dto.*;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.user.dto.GetUserProfileClubRes;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.List;

@Service
public class UserProfileService {

    private MemberStatusRepository memberStatusRepository;
    private UserProfileRepository userProfileRepository;
    private UserRepository userRepository;

    public UserProfileService(MemberStatusRepository memberStatusRepository, UserProfileRepository userProfileRepository,
                              UserRepository userRepository) {
        this.memberStatusRepository = memberStatusRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public GetUserProfileClubRes getUserProfileClub(Long userProfileIdx) throws BaseException {
        Optional<UserProfileEntity> optionalUserProfile = userProfileRepository.findByUserProfileIdx(userProfileIdx);
        if (optionalUserProfile.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }

        Optional<MemberStatusEntity> optionalStatus = null;
        try {
            optionalStatus = memberStatusRepository.
                    findByUserProfileIdx_UserProfileIdxAndApplyStatusAndStatus(userProfileIdx, "ACCEPTED", "active");
        } catch (IncorrectResultSizeDataAccessException e){
            // 프로필이 두개 이상의 그룹에 들어가 있을 때 (비정상 활동)
            throw new BaseException(BaseResponseStatus.ERROR_DUPLICATE_CLUB);
        }
        if (optionalStatus.isEmpty()) {
            // 프로필이 그룹에 들어가있지 않을 때
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
        }
        return new GetUserProfileClubRes(optionalStatus.get().getClubIdx().getClubIdx(), optionalStatus.get().getClubIdx().getName());
    }

    public GetProfileRes getUserProfile(Principal principal, Long profileIdx) throws BaseException {
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        // userProfileIdx 존재 안할때
        UserProfileEntity userProfileList = userProfileRepository.findByUserProfileIdx(profileIdx).get();
        if (userProfileList == null) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }
        GetProfileRes userProfile = new GetProfileRes();
        userProfile.setUserProfileIdx(profileIdx);
        userProfile.setNickname(userProfileList.getNickName());
        userProfile.setStatusMsg(userProfileList.getStatusMsg());
        userProfile.setIsAlarmOn(userProfileList.getIsAlarmOn());
        userProfile.setImgUrl(userProfileList.getImgURL());
        userProfile.setUserName(userProfileList.getUserIdx().getName());
        userProfile.setEmail(userProfileList.getUserIdx().getEmail());

        Optional<MemberStatusEntity> memberStatus = memberStatusRepository.findByUserProfileIdx(profileIdx);
        if (memberStatus.isEmpty()) {
            userProfile.setClubIdx(0L);
            userProfile.setClubName("그룹에 속하지 않습니다.");
        }
        else if (memberStatus.get().getApplyStatus().equals("ACCEPTED")) {
            ClubEntity clubEntity = memberStatus.get().getClubIdx();
            userProfile.setClubIdx(clubEntity.getClubIdx());
            userProfile.setClubName(clubEntity.getName());
        }
        return userProfile;
    }

    public List<GetProfileListRes> viewProfile(Principal principal) throws BaseException {
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        // userIdx가 생성한 프로필 idx 다 조회
        Optional<List<UserProfileEntity>> userProfileList = userProfileRepository.findAllByUserIdx(optional.get());
        List<GetProfileListRes> getProfileList = new ArrayList<>();
        // 조회한 프로필 Id들 Dto에 담기
        for (UserProfileEntity profile : userProfileList.get()) {
            GetProfileListRes getProfileRes = new GetProfileListRes();
            getProfileRes.setUserProfileIdx(profile.getUserProfileIdx());
            getProfileRes.setNickname(profile.getNickName());
            getProfileRes.setStatusMsg(profile.getStatusMsg());
            getProfileRes.setIsAlarmOn(profile.getIsAlarmOn());
            getProfileRes.setImgUrl(profile.getImgURL());
            getProfileRes.setUserName(optional.get().getName());
            getProfileRes.setEmail(optional.get().getEmail());
            getProfileList.add(getProfileRes);
        }
        return getProfileList;
    }
    public Long addProfile(Principal principal, PostProfileReq profileReq) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();
        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .userIdx(userEntity)
                .nickName(profileReq.getNickname())
                .imgURL(profileReq.getImgUrl())
                .isAlarmOn(profileReq.getIsAlarmOn())
                .statusMsg(profileReq.getStatusMsg())
                .build();
        userProfileEntity = userProfileRepository.save(userProfileEntity);
        return userProfileEntity.getUserProfileIdx();
    }

    public void changeProfile(Principal principal, PatchProfileReq profileReq) throws BaseException {
        if(userRepository.findByEmail(principal.getName()).isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        Optional<UserEntity> UserEntity = userRepository.findByEmail(principal.getName());
        Optional<UserProfileEntity> profile = userProfileRepository.findByUserIdx(UserEntity.get());
        UserProfileEntity newProfile = profile.get();
        if (!(profileReq.getNickName() == null || profileReq.getNickName().length() == 0)) {
            newProfile.changeNickName(profileReq.getNickName());
        }
        if (!(profileReq.getImgUrl() == null || profileReq.getImgUrl().length() == 0)) {
            newProfile.changeImgUrl(profileReq.getImgUrl());
        }
        if (!(profileReq.getStatusMsg() == null || profileReq.getStatusMsg().length() == 0)) {
            newProfile.changeStatusMsg(profileReq.getStatusMsg());
        }
        userProfileRepository.save(newProfile);
    }
}
