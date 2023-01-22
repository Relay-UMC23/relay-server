package com.example.relayRun.user.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.dto.GetUserProfileClubRes;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    private MemberStatusRepository memberStatusRepository;

    public UserProfileService(MemberStatusRepository memberStatusRepository) {
        this.memberStatusRepository = memberStatusRepository;
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
}
