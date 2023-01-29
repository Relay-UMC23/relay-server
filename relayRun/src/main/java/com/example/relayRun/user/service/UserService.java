package com.example.relayRun.user.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.jwt.entity.RefreshTokenEntity;
import com.example.relayRun.jwt.repository.RefreshTokenRepository;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.RecordRepository;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.user.dto.*;
import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.util.Role;
import org.apache.catalina.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.Time;
import java.util.*;

import static com.example.relayRun.util.ValidationRegex.isRegexEmail;
import static com.example.relayRun.util.ValidationRegex.isRegexPwd;
import static java.util.Optional.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ClubRepository clubRepository;
    @Autowired
    private final RecordRepository recordRepository;
    @Autowired
    private final RunningRecordRepository runningRecordRepository;
    @Autowired
    private final MemberStatusRepository memberStatusRepository;
    @Autowired
    private final TimeTableRepository timeTableRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private JavaMailSender javaMailSender;

    private RedisUtil redisUtil;

    private final String ePw = createKey();
    private String id = "codusl100@naver.com";


//    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository,
//                       PasswordEncoder passwordEncoder, TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository,
//                       ClubRepository clubRepository, RecordRepository recordRepository, RunningRecordRepository runningRecordRepository,
//                       MemberStatusRepository memberStatusRepository, TimeTableRepository timeTableRepository,
//                       AuthenticationManagerBuilder authenticationManagerBuilder, JavaMailSender javaMailSender, RedisUtil redisUtil){
//        this.userRepository = userRepository;
//        this.userProfileRepository = userProfileRepository;
//        this.clubRepository = clubRepository;
//        this.recordRepository = recordRepository;
//        this.runningRecordRepository = runningRecordRepository;
//        this.memberStatusRepository = memberStatusRepository;
//        this.timeTableRepository = timeTableRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.tokenProvider = tokenProvider;
//        this.refreshTokenRepository = refreshTokenRepository;
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//        this.javaMailSender = javaMailSender;
//        this.redisUtil = redisUtil;
//    }

    // 회원가입
    public TokenDto signIn(PostUserReq user) throws BaseException {
        if(user.getEmail() == null || user.getPwd() == null){
            throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(isHaveEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
        String password = user.getPwd();
        if(!isRegexPwd(password)){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PWD);
        }
        try{
            String encodedPwd = passwordEncoder.encode(user.getPwd());
            user.setPwd(encodedPwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        UserEntity userEntity = UserEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .pwd(user.getPwd())
                .loginType(LoginType.BASIC)
                .role(Role.ROLE_USER)
                .build();
        user.setPwd(password);

        userEntity = userRepository.save(userEntity);
        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .nickName("기본 닉네임")
                .imgURL("기본 이미지")
                .statusMsg("안녕하세요")
                .userIdx(userEntity)
                .build();
        userProfileRepository.save(userProfileEntity);
        return token(user);

    }

    // 로그인
    public TokenDto logIn(PostLoginReq user) throws BaseException {
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        // 이메일 DB에서 확인
        Optional<UserEntity> optional = userRepository.findByEmail(user.getEmail());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        else{
            // 소셜 타입 확인
            UserEntity userEntity = optional.get();
            if(!userEntity.getLoginType().equals(LoginType.BASIC)){
                throw new BaseException(BaseResponseStatus.SOCIAL);
            }
            // 입력받은 pwd와 entity pwd와 비교
            if(passwordEncoder.matches(user.getPwd(), userEntity.getPwd())) {
                return loginToken(user);
            }else{
                throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
            }

        }
    }

    public boolean isHaveEmail(String email) { return userRepository.existsByEmail(email); }


    public TokenDto token(PostUserReq user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPwd());
        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken 저장
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. 토큰 발급
        return tokenDto;
    }

    public TokenDto loginToken(PostLoginReq user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPwd());
        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken 저장
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. 토큰 발급
        return tokenDto;
    }


    public TokenDto reissue(TokenDto tokenRequestDto) { //재발급
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByKeyId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshTokenEntity newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }

    public void deleteUser(Principal principal) throws BaseException {
        // User 조회
        Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isPresent()) {
            // UserEntity
            UserEntity user = optionalUser.get();
            String email = user.getEmail();
            Long userIdx = user.getUserIdx();

            // Refresh Token 삭제
            Optional<RefreshTokenEntity> optionalRT = refreshTokenRepository.findByKeyId(email);
            if(optionalRT.isPresent()) {
                refreshTokenRepository.delete(optionalRT.get());
            }
            // User 삭제
            try {
                userRepository.deleteById(userIdx);
            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.DELETE_USER_ERROR);
                }
            }
        else {
            throw new BaseException(BaseResponseStatus.DELETE_USER_ERROR);
        }
    }


    public GetUserRes getUserInfo(Principal principal) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userEntity = optionalUserEntity.get();
        GetUserRes result = new GetUserRes(
                userEntity.getEmail(),
                userEntity.getName()
        );
        return result;
    }

    public void changePwd(Principal principal, PatchUserPwdReq user) throws BaseException {
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if(user.getNewPwd().length() == 0 || user.getNewPwd() == null){
            throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }

        UserEntity userEntity = optional.get();
        if(!user.getNewPwd().equals(user.getNewPwdCheck())){
            throw new BaseException(BaseResponseStatus.PATCH_PASSWORD_CHECK_WRONG);
        }

        if(!userEntity.getLoginType().equals(LoginType.BASIC)){
            throw new BaseException(BaseResponseStatus.SOCIAL);
        }
        if(!isRegexPwd(user.getNewPwd())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PWD);
        }

        // 새 비밀번호 encryption
        String encodedPwd;
        try{
            encodedPwd = passwordEncoder.encode(user.getNewPwd());
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        userEntity.changePwd(encodedPwd);
        userRepository.save(userEntity);
    }

    public List<GetProfileRes> viewProfile(Principal principal) throws BaseException {
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        // userIdx가 생성한 프로필 idx 다 조회
        List<UserProfileEntity> userProfileList = userProfileRepository.findAllByUserIdx(optional.get()).get();
        List<GetProfileRes> getProfileList = new ArrayList<>();
        // 조회한 프로필 Id들 Dto에 담기
        for (UserProfileEntity profile : userProfileList) {
            GetProfileRes getProfileRes = new GetProfileRes();
            getProfileRes.setUserProfileIdx(profile.getUserProfileIdx());
            getProfileRes.setNickname(profile.getNickName());
            getProfileRes.setStatusMsg(profile.getStatusMsg());
            getProfileRes.setIsAlarmOn(profile.getIsAlarmOn());
            getProfileRes.setImgUrl(profile.getImgURL());
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

    public MimeMessage createMessage(String to)throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : " + ePw);
        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("이어달리기 인증 코드 발급 안내"); //메일 제목

        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이어달리기 인증 번호</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"이어달리기 팀")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    // 메일 발송
    public String sendSimpleMessage(Principal principal, String to)throws Exception {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        MimeMessage message = createMessage(to);
        String email = optionalUserEntity.get().getEmail();
        try{
            javaMailSender.send(message); // 메일 발송
            //    Redis로 유효기간 설정하기
            // 유효 시간(5분)동안 {email, authKey} 저장
            redisUtil.setDataExpire(ePw, email, 60 * 5L);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw; // 메일로 보냈던 인증 코드를 서버로 리턴
    }

    // 인증 번호 확인
    public boolean confirmEmail(Principal principal, GetEmailCodeReq code) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if (optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        String user = redisUtil.getData(code.getCode());
        log.info("유저 정보 : " + user);
        if (user == null || user.length() == 0) {
            return false;
        }
        return true;
    }
}

