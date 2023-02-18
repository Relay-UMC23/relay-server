package com.example.relayRun.fcm.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.fcm.dto.NotificationMessage;
import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.fcm.repository.UserDeviceRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FCMService {
    FCMSenderSerivce senderService;
    UserRepository userRepository;
    UserDeviceRepository userDeviceRepository;
    UserProfileRepository userProfileRepository;
    MemberStatusRepository memberStatusRepository;

    TimeTableRepository timeTableRepository;

    public FCMService(
            FCMSenderSerivce senderService,
            UserRepository userRepository,
            MemberStatusRepository memberStatusRepository,
            UserProfileRepository userProfileRepository,
            UserDeviceRepository userDeviceRepository,
            TimeTableRepository timeTableRepository
    )
    {
        this.senderService = senderService;
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.userProfileRepository = userProfileRepository;
        this.timeTableRepository = timeTableRepository;
    }

    public PostDeviceRes saveDeviceToken(Principal principal, PostDeviceReq req) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        UserEntity user = optionalUser.get();
        Optional<UserDeviceEntity> optionalDevice = userDeviceRepository.findByUserDeviceTokenAndUserIdx(req.getUserDeviceID(), user);
        if (optionalDevice.isPresent())
            throw new BaseException(BaseResponseStatus.POST_ALARM_DUPLICATED_TOKEN);
        UserDeviceEntity userDevice = UserDeviceEntity.builder()
                .userDeviceToken(req.getUserDeviceID())
                .userIdx(user)
                .build();
        userDeviceRepository.save(userDevice);
        senderService.sendMessageByToken(req.getUserDeviceID(),
                NotificationMessage.builder()
                        .title("저장 성공")
                        .body("디바이스 토큰 저장")
                        .build()
        );
        return PostDeviceRes.builder()
                .status("성공")
                .build();
    }

    public void deleteDeviceToken(Principal principal, PostDeviceReq req) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        UserEntity user = optionalUser.get();
        Optional<UserDeviceEntity> optionalDevice = userDeviceRepository.findByUserDeviceTokenAndUserIdx(req.getUserDeviceID(), user);
        if (optionalDevice.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        log.info(user.getUserIdx() + " 디바이스 토큰 삭제 중");
        UserDeviceEntity userDevice = optionalDevice.get();
        userDeviceRepository.delete(userDevice);
    }

    public void sendTimeToRunMessage(Long memberStatusIdx, LocalTime start) {
        Optional<MemberStatusEntity> optionalMember = memberStatusRepository.findById(memberStatusIdx);
        if (optionalMember.isEmpty()) {
            log.error("유효하지 않은 memberStatusIdx 입니다.");
            return ;
        }
        MemberStatusEntity member = optionalMember.get();
        Optional<UserProfileEntity> optionalProfile = userProfileRepository.findById(member.getUserProfileIdx().getUserProfileIdx());
        if (optionalProfile.isEmpty()) {
            log.error("유효하지 않은 userProfileIdx 입니다.");
            return ;
        }
        UserProfileEntity profile = optionalProfile.get();
        Optional<UserEntity> optionalUser = userRepository.findById(profile.getUserIdx().getUserIdx());
        if (optionalUser.isEmpty()) {
            log.error("유효하지 않은 userIdx 입니다.");
            return ;
        }
        UserEntity user = optionalUser.get();
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .title("뛸 시간입니다!")
                    .body(start.toString() + "부터 달리기 시작하세요!")
                    .build();
            senderService.sendMessageById(user.getUserIdx(), message);
//            senderService.sendMessageToAll(message);
        } catch (BaseException e) {
            log.error(e.getMessage());
        }
    }

    public void sendBatonTouchMessage(Long fromProfile, int day, LocalTime end) throws BaseException {
        Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository
                .findByUserProfileIdx_UserProfileIdxAndApplyStatusAndStatus(fromProfile, "ACCEPTED", "active");
        if (optionalMemberStatus.isEmpty())
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER_STATUS);
        MemberStatusEntity memberStatus = optionalMemberStatus.get();
        // 다음 시간표를 가져옴.
        Optional<TimeTableEntity> optionalTimeTable = timeTableRepository.findByClubAndDayAndAfterTime(
                memberStatus.getClubIdx().getClubIdx(), day, end.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        if (optionalTimeTable.isEmpty()) {
            // 오늘 다음 완주자 없을 시 그룹원 모두에게 알람
            NotificationMessage message =  NotificationMessage.builder()
                    .title("오늘의 런닝 그룹 완주!")
                    .body(memberStatus.getClubIdx().getName() + "그룹 완주를 축하합니다. 내일도 힘내봐요!")
                    .build();
            senderService.sendMessageToGroup(memberStatus.getClubIdx().getClubIdx(), message);
//            senderService.sendMessageToAll(message);
        }else {
            TimeTableEntity timeTable = optionalTimeTable.get();
            NotificationMessage message = NotificationMessage.builder()
                    .title("바톤터치!")
                    .body(memberStatus.getUserProfileIdx().getNickName() + "님이 바톤을 넘겼습니다! 꼭 달려주세요!")
                    .build();
            senderService.sendMessageById(
                    timeTable.getMemberStatusIdx().getUserProfileIdx().getUserIdx().getUserIdx(),
                    message
            );
//            senderService.sendMessageToAll(message);
        }
    }
}
