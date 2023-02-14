package com.example.relayRun.test;

import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.dto.TimeTableDTO;
import com.example.relayRun.user.dto.PostLoginReq;
import com.example.relayRun.user.dto.PostUserReq;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.user.service.UserService;
import com.example.relayRun.util.GoalType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Dummy {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    static RestTemplate restTemplate = new RestTemplate();
    static ObjectMapper objectMapper = new ObjectMapper();
    static JSONParser parser = new JSONParser();

    static String domain = "http://3.38.32.124";


    public static void main(String[] args) throws JsonProcessingException, ParseException {
        objectMapper.registerModule(new JavaTimeModule());

        String token;
        Long profileIdx;
        Long clubIdx;

        // 방장용
//        signUpSuccess("");

//        token = login("");
//        profileIdx = getProfile(token);
//
//        makeClub(token, profileIdx);
        clubIdx = 62L; // 하드코딩


//        for (int i=1; i<=7; i++) {
//            // signup
//            signUpSuccess("-" + String.valueOf(i));
//            // 왜 계속 401 ... 근데 잘 들어가긴 함...
//        }


        for (int i=1; i<=7; i++) {
            // login
            token = login("-"+String.valueOf(i));
            // get profile
            profileIdx = getProfile(token);
            // apply to club
            applyClub(token, i, profileIdx, clubIdx);
        }
    }

    public static void signUpSuccess(String ord) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json;charset=utf-8");
        String name = "demo2";
        String dom = "@naver.com";

        String signUpBody = objectMapper.writeValueAsString(
                PostUserReq.builder()
                        .email(name+ord+dom)
                        .pwd("qwer1234")
                        .name(name+ord)
                        .build()
        );

        HttpEntity<MultiValueMap<String, String>> signUpRequest = new HttpEntity(signUpBody, headers);

        String uri = domain + "/users/sign-in";
        ResponseEntity<String> response = restTemplate
                .exchange(
                    uri,
                    HttpMethod.POST,
                    signUpRequest,
                    String.class
        );

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

    }

    public static String login(String ord) throws JsonProcessingException, ParseException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json;charset=utf-8");

        String name = "demo2";
        String dom = "@naver.com";
        
        String loginBody = objectMapper.writeValueAsString(
                PostLoginReq.builder()
                        .email(name+ord+dom)
                        .pwd("qwer1234")
                        .build()
        );

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity(loginBody, headers);

        String uri = domain + "/users/logIn";
        ResponseEntity<String> response = restTemplate
                .exchange(
                        uri,
                        HttpMethod.POST,
                        loginRequest,
                        String.class
                );

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
        JSONObject result = (JSONObject) parser.parse(jsonObject.get("result").toString());

        return result.get("accessToken").toString();
    }

    public static Long getProfile(String token) throws JsonProcessingException, ParseException {
        System.out.println("Dummy.getProfile");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json;charset=utf-8");
        headers.add("Authorization", "Bearer "+token);



        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity("", headers);

        String uri = domain + "/users/profileList";
        ResponseEntity<String> response = restTemplate
                .exchange(
                        uri,
                        HttpMethod.GET,
                        request,
                        String.class
                );

        JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
        JSONArray result = (JSONArray) parser.parse(jsonObject.get("result").toString());
        JSONObject first = (JSONObject) result.get(0);

        return (Long) first.get("userProfileIdx");
    }

    public static void makeClub(String token, Long profileIdx) throws JsonProcessingException {
        System.out.println("Dummy.makeClub");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json;charset=utf-8");
        headers.add("Authorization", "Bearer "+token);

        List<TimeTableDTO> timeTableDTOList = new ArrayList<>();
        timeTableDTOList.add(TimeTableDTO.builder()
                .day(3)
                .start(LocalTime.of(10, 0, 0))
                .end(LocalTime.of(20, 0, 0))
                .goalType(GoalType.TIME)
                .goal(300F)
                .build());

        String clubBody = objectMapper.writeValueAsString(
                PostClubReq.builder()
                        .name("데모데이 그룹1")
                        .content("안녕하세요 런닝MEN 팀입니다!")
                        .goalType(GoalType.TIME)
                        .goal(2400F)
                        .hostIdx(profileIdx)
                        .level(3)
                        .maxNum(8)
                        .timeTable(timeTableDTOList)
                        .build()
        );

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(clubBody, headers);

        String uri = domain + "/clubs";
        ResponseEntity<String> response = restTemplate
                .exchange(
                        uri,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        System.out.println(response.getBody());

    }

    public static List<TimeTableDTO> timetable(int num) {

        List<TimeTableDTO> timeTableDTOList = new ArrayList<>();

        switch (num) {
            case 1:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(2)
                        .start(LocalTime.of(9, 0, 0))
                        .end(LocalTime.of(10, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(1800F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(4)
                        .start(LocalTime.of(9, 0, 0))
                        .end(LocalTime.of(10, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(1800F)
                        .build());
                break;
            case 2:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(1)
                        .start(LocalTime.of(10, 30, 0))
                        .end(LocalTime.of(12, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(5)
                        .start(LocalTime.of(16, 0, 0))
                        .end(LocalTime.of(17, 30, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                break;
            case 3:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(5)
                        .start(LocalTime.of(10, 30, 0))
                        .end(LocalTime.of(12, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(1)
                        .start(LocalTime.of(17, 30, 0))
                        .end(LocalTime.of(19, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                break;
            case 4:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(2)
                        .start(LocalTime.of(12, 0, 0))
                        .end(LocalTime.of(14, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(3600F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(4)
                        .start(LocalTime.of(12, 30, 0))
                        .end(LocalTime.of(14, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(3600F)
                        .build());
                break;
            case 5:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(1)
                        .start(LocalTime.of(14, 30, 0))
                        .end(LocalTime.of(16, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(2)
                        .start(LocalTime.of(14, 30, 0))
                        .end(LocalTime.of(16, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                break;
            case 6:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(4)
                        .start(LocalTime.of(15, 00, 0))
                        .end(LocalTime.of(16, 30, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(2)
                        .start(LocalTime.of(17, 0, 0))
                        .end(LocalTime.of(18, 30, 0))
                        .goalType(GoalType.TIME)
                        .goal(2700F)
                        .build());
                break;
            case 7:
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(2)
                        .start(LocalTime.of(19, 0, 0))
                        .end(LocalTime.of(20, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(1800F)
                        .build());
                timeTableDTOList.add(TimeTableDTO.builder()
                        .day(5)
                        .start(LocalTime.of(19, 0, 0))
                        .end(LocalTime.of(20, 0, 0))
                        .goalType(GoalType.TIME)
                        .goal(1800F)
                        .build());
                break;
        }
        return timeTableDTOList;
    }

    public static void applyClub(String token, int n, Long profileIdx, Long clubIdx) throws JsonProcessingException {
        System.out.println("applyClub) profileIdx = " + profileIdx + ", n = " + n);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json;charset=utf-8");
        headers.add("Authorization", "Bearer "+token);

        List<TimeTableDTO> timeTableDTOList = timetable(n);

        String clubBody = objectMapper.writeValueAsString(
                PostMemberStatusReq.builder()
                        .timeTables(timeTableDTOList)
                        .userProfileIdx(profileIdx)
                        .build()
        );

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(clubBody, headers);

        String uri = domain + "/clubs/member-status/" + clubIdx;
        ResponseEntity<String> response = restTemplate
                .exchange(
                        uri,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        System.out.println(response.getBody());
    }

}
