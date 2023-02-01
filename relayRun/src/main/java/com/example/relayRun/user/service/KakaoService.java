package com.example.relayRun.user.service;

import com.example.relayRun.jwt.dto.TokenDto;
import com.example.relayRun.user.dto.PostLoginReq;
import com.example.relayRun.user.dto.SocialUserDto;
import com.example.relayRun.user.dto.kakao.KakaoProfile;
import com.example.relayRun.user.dto.kakao.OAuthToken;
import com.example.relayRun.user.entity.LoginType;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.util.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import static com.example.relayRun.util.ValidationRegex.isRegexEmail;

@Service
@Slf4j
public class KakaoService {

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public KakaoService(UserService userService, UserRepository userRepository, UserProfileRepository userProfileRepository,
                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //인가 코드 가져오기
    @Transactional
    public String getAuthorize(String code) throws BaseException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String accessToken = oAuthToken.getAccess_token();

        if(accessToken == null) {
            throw new BaseException(BaseResponseStatus.KAKAO_ACCESS_TOKEN_EMPTY);
        }

        return accessToken;
    }

    //카카오 프로필 가져와서 가입/로그인 진행
    @Transactional
    public TokenDto getProfile(String kakaoAccessToken) throws BaseException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String kakaoName = kakaoProfile.getProperties().getNickname();
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();

        if (kakaoName == null || kakaoEmail == null) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_GET_KAKAO_PROFILE);
        }

        boolean isExistUser = userRepository.existsByEmail(kakaoEmail);
        String encodedPwd = passwordEncoder.encode("1234");

        if(!isExistUser) {
            SocialUserDto user = SocialUserDto.builder()
                    .name(kakaoName)
                    .email(kakaoEmail)
                    .pwd(encodedPwd)
                    .build();
            signIn(user);
        }

        PostLoginReq user = new PostLoginReq();
        user.setEmail(kakaoEmail);
        user.setPwd("1234");

        return userService.loginToken(user);
    }

    //회원가입
    public void signIn(SocialUserDto user) throws BaseException {
        if (user.getEmail() == null || user.getPwd() == null)
            throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        if (!isRegexEmail(user.getEmail()))
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);

        UserEntity userEntity = UserEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .pwd(user.getPwd())
                .loginType(LoginType.KAKAO)
                .role(Role.ROLE_USER)
                .build();

        userEntity = userRepository.save(userEntity);

        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .nickName("기본 닉네임")
                .imgURL("기본 이미지")
                .statusMsg("안녕하세요")
                .userIdx(userEntity)
                .build();

        userProfileRepository.save(userProfileEntity);
    }
}