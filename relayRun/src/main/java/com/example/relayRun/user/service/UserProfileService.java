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
import org.springframework.stereotype.Service;

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
        ClubEntity clubEntity = null;
        List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByUserProfileIdx_UserProfileIdx(userProfileIdx);
        for (MemberStatusEntity memberStatusEntity : memberStatusEntityList) {
            if (memberStatusEntity.getApplyStatus().equals("ACCEPTED")) {
                clubEntity = memberStatusEntity.getClubIdx();
                break;
            }
        }
        if (clubEntity != null)
            return new GetUserProfileClubRes(clubEntity.getClubIdx(), clubEntity.getName());
        throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
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
        if (memberStatus == null) {
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
        List<UserProfileEntity> userProfileList = userProfileRepository.findAllByUserIdx(optional.get()).get();
        List<GetProfileListRes> getProfileList = new ArrayList<>();
        // 조회한 프로필 Id들 Dto에 담기
        for (UserProfileEntity profile : userProfileList) {
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
}
