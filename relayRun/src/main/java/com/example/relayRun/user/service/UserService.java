package com.example.relayRun.user.service;

import com.example.relayRun.jwt.TokenProvider;
import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.jwt.entity.RefreshTokenEntity;
import com.example.relayRun.jwt.repository.RefreshTokenRepository;
import com.example.relayRun.user.dto.*;
import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;

import static com.example.relayRun.util.ValidationRegex.isRegexEmail;
import static com.example.relayRun.util.ValidationRegex.isRegexPwd;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    private JavaMailSender javaMailSender;

    private RedisUtil redisUtil;

    private String bucketURL = "https://team23-bucket.s3.ap-northeast-2.amazonaws.com/public/profile";

    private HashMap<Integer, String> avatar = new HashMap<>() {{
        put(1, bucketURL + "/1.png");
        put(2, bucketURL + "/2.png");
        put(3, bucketURL + "/3.png");
        put(4, bucketURL + "/4.png");
        put(5, bucketURL + "/5.png");
    }};


    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository,
                       PasswordEncoder passwordEncoder, TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManagerBuilder authenticationManagerBuilder, JavaMailSender javaMailSender, RedisUtil redisUtil){
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }

    // ????????????
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

        userRepository.save(userEntity);

        Random random = new Random();
        // ????????? ?????? ??????
        UserProfileEntity userProfileEntity = buildProfile(
                userEntity,
                userEntity.getName(),
                avatar.get(random.nextInt(5) + 1),
                "y",
                "???????????????");

        userProfileRepository.save(userProfileEntity);

        return token(user);

    }

    // ?????????
    public TokenDto logIn(PostLoginReq user) throws BaseException {
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        // ????????? DB?????? ??????
        Optional<UserEntity> optional = userRepository.findByEmail(user.getEmail());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        else{
            // ?????? ?????? ??????
            UserEntity userEntity = optional.get();
            if(!userEntity.getLoginType().equals(LoginType.BASIC)){
                throw new BaseException(BaseResponseStatus.SOCIAL);
            }
            // ???????????? pwd??? entity pwd??? ??????
            if(passwordEncoder.matches(user.getPwd(), userEntity.getPwd())) {
                return loginToken(user);
            }else{
                throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
            }

        }
    }

    public boolean isHaveEmail(String email) { return this.userRepository.existsByEmail(email); }


    public TokenDto token(PostUserReq user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPwd());
        // 2. ????????? ?????? (????????? ???????????? ??????) ??? ??????????????? ??????
        //    authenticate ???????????? ????????? ??? ??? CustomUserDetailsService ?????? ???????????? loadUserByUsername ???????????? ?????????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken ??????
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. ?????? ??????
        return tokenDto;
    }

    public TokenDto loginToken(PostLoginReq user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPwd());
        // 2. ????????? ?????? (????????? ???????????? ??????) ??? ??????????????? ??????
        //    authenticate ???????????? ????????? ??? ??? CustomUserDetailsService ?????? ???????????? loadUserByUsername ???????????? ?????????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken ??????
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. ?????? ??????
        return tokenDto;
    }


    public TokenDto reissue(TokenDto tokenRequestDto) { //?????????
        // 1. Refresh Token ??????
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token ??? ???????????? ????????????.");
        }

        // 2. Access Token ?????? Member ID ????????????
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. ??????????????? Member ID ??? ???????????? Refresh Token ??? ?????????
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByKeyId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("???????????? ??? ??????????????????."));

        // 4. Refresh Token ??????????????? ??????
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("????????? ?????? ????????? ???????????? ????????????.");
        }

        // 5. ????????? ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. ????????? ?????? ????????????
        RefreshTokenEntity newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // ?????? ??????
        return tokenDto;
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

        // ??? ???????????? encryption
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
        // userIdx??? ????????? ????????? idx ??? ??????
        List<UserProfileEntity> userProfileList = userProfileRepository.findAllByUserIdx(optional.get());
        List<GetProfileRes> getProfileList = new ArrayList<>();
        // ????????? ????????? Id??? Dto??? ??????
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

        UserProfileEntity userProfileEntity = buildProfile(userEntity,
                                        profileReq.getNickname(),
                                        profileReq.getImgUrl(),
                                        profileReq.getIsAlarmOn(),
                                        profileReq.getStatusMsg());

        return userProfileEntity.getUserProfileIdx();
    }

    /**
     *
     * @param userEntity
     * @param nickname
     * @param imgURL
     * @param isAlarmOn
     * @param statusMsg
     * @return UserProfileEntity
     */
    public UserProfileEntity buildProfile(UserEntity userEntity, String nickname, String imgURL, String isAlarmOn, String statusMsg) {
        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .userIdx(userEntity)
                .nickName(nickname)
                .imgURL(imgURL)
                .isAlarmOn(isAlarmOn)
                .statusMsg(statusMsg)
                .build();
        userProfileEntity = userProfileRepository.save(userProfileEntity);
        return userProfileEntity;
    }

    public void changeAlarm(Principal principal, Long profileIdx) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        // principal??? userIdx??? userProfileIdx??? userIdx ??? ?????????
        Optional<UserProfileEntity> profileOptional = userProfileRepository.findByUserProfileIdx(profileIdx);
        System.out.println("?????? Idx : " + optionalUserEntity.get().getUserIdx());
        System.out.println("???????????? ?????? Idx : "+ profileOptional.get().getUserIdx().getUserIdx());
        if(!profileOptional.get().getUserIdx().getUserIdx().equals(optionalUserEntity.get().getUserIdx())) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EQUALS);
        }
        UserProfileEntity UserProfile = userProfileRepository.findByUserProfileIdx(profileIdx).get();
        if (UserProfile.getIsAlarmOn().equals("y")) {
            UserProfile.setIsAlarmOn("n");
            userProfileRepository.save(UserProfile);
        }
        else if (UserProfile.getIsAlarmOn().equals("n")) {
            UserProfile.setIsAlarmOn("y");
            userProfileRepository.save(UserProfile);
        }
    }
    public MimeMessage createMessage(String from, String to, String ePw) throws MessagingException, UnsupportedEncodingException {
        log.info("????????? ?????? : "+ to);
        log.info("?????? ?????? : " + ePw);
        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to ????????? ??????
        message.setSubject("??????????????? ?????? ?????? ?????? ??????"); //?????? ??????

        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">??????????????? ?????? ??????</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">?????? ?????? ????????? ??????????????????.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //??????, charset??????, subtype
        message.setFrom(new InternetAddress(from,"??????????????? ???")); //????????? ????????? ?????? ??????, ????????? ?????? ??????

        return message;
    }

    // ???????????? ?????????
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // ???????????? 6??????
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    // ?????? ??????
    public String sendSimpleMessage(Principal principal, String from)throws Exception {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if(optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserProfileEntity UserProfile = userProfileRepository.findByUserIdx(optionalUserEntity.get()).get();
        if (UserProfile.getIsAlarmOn().equals("y")) {
            UserProfile.setIsAlarmOn("n");
            userProfileRepository.save(UserProfile);
        }
        else if (UserProfile.getIsAlarmOn().equals("n")) {
            UserProfile.setIsAlarmOn("y");
            userProfileRepository.save(UserProfile);
        }
        String ePw = createKey(); // ????????? ?????? ??????
        String to = optionalUserEntity.get().getEmail();
        MimeMessage message = createMessage(from, to, ePw);
        try{
            javaMailSender.send(message); // ?????? ??????
            //    Redis??? ???????????? ????????????
            // ?????? ??????(5???)?????? {email, authKey} ??????
            redisUtil.setDataExpire(ePw, to, 60 * 5L);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw; // ????????? ????????? ?????? ????????? ????????? ??????
    }

    // ?????? ?????? ??????
    public boolean confirmEmail(Principal principal, GetEmailCodeReq code) throws BaseException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(principal.getName());
        if (optionalUserEntity.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        String user = redisUtil.getData(code.getCode());
        log.info("?????? ?????? : " + user);
        if (user == null || user.length() == 0 || !user.equals(optionalUserEntity.get().getEmail())) {
            return false;
        }
        return true;
    }
}

