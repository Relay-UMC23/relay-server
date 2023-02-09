package com.example.relayRun.util;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    EMPTY_TOKEN(false, 1001, "토큰을 확인해주세요."),
    DUPLICATE_NICKNAME(false, 2000, "닉네임이 중복되었습니다."),
    DUPLICATE_EMAIL(false, 2001, "이메일이 중복되었습니다."),
    EMPTY_JWT(false, 2002, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2003, "유효하지 않은 JWT입니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2004, "아이디를 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2005, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2006, "비밀번호가 틀렸습니다."),
    FAILED_TO_LOGIN(false, 2007, "없는 아이디이거나 비밀번호가 틀렸습니다."),
    FAILED_TO_SEARCH(false, 2008, "검색을 실패하였습니다."),
    FAILED_TO_FIND_URL(false, 2009,"요청할 수 없는 url입니다."),

    // 달리기단;
    RECORD_UNAVAILABLE(false, 2100,"기록이 존재하지 않습니다."),
    INVALID_DATE_FORMAT(false, 2101, "yyyy-mm-dd의 날짜 형식을 입력해주세요."),

    CLUB_UNAVAILABLE(false, 2200, "존재하지 않는 그룹입니다."),

    FAILED_TO_FIND_USER(false, 2010, "유효하지 않은 인증 코드입니다."),
    /*
     * 4000: [POST]
     * */
    PASSWORD_ENCRYPTION_ERROR(false, 4001, "비밀번호 암호화에 실패했습니다."),
    DATABASE_ERROR(false, 4002, "데이터베이스 연결에 실패하였습니다."),
    POST_CLUBS_FAIL(false, 4020, "그룹 생성에 실패하였습니다."),
    POST_USERS_PROFILES_EMPTY(false, 4021, "유효하지 않은 프로필 아이디입니다."),
    POST_USERS_PROFILES_EQUALS(false, 4022, "해당하는 유저의 프로필 아이디가 아닙니다."),
    POST_CLUBS_NAME_EMPTY(false, 4023, "그룹 이름을 입력해주세요."),
    POST_CLUBS_CONTENTS_EMPTY(false,4024, "그룹 소개를 입력해주세요."),
    POST_CLUBS_LEVEL_EMPTY(false, 4025, "그룹 레벨을 설정해주세요."),
    POST_USERS_EMPTY(false, 4777, "공백 없이 입력해주세요."),
    POST_USERS_EMPTY_EMAIL(false, 4778,"이메일을 입력해주세요"),
    POST_USERS_EMPTY_NAME(false, 4779,"이름을 입력해주세요"),

    POST_RECORD_INVALID_CLUB_ACCESS(false, 4900, "해당 그룹에 가입되지 않은 프로필입니다."),
    POST_RECORD_INVALID_RECORD_ID(false, 4901, "유효하지 않은 레코드 아이디입니다."),
    POST_PARSE_ERROR(false, 4902, "포인트 형식에 맞지 않습니다."),

    POST_RECORD_NO_TIMETABLE(false, 4903, "기록에 맞는 시간표가 없습니다."),
    POST_RECORD_ALREADY_FINISH(false, 4904, "이미 끝난 기록입니다."),
    POST_RECORD_NO_PROFILE_IDX(false, 4905, "존재하지 않는 프로필 아이디입니다."),
    POST_RECORD_NOT_MATCH_PARAM_PRINCIPAL(false, 4906, "요청한 프로필 아이디와 로그인 아이디가 일치하지 않습니다."),
    POST_USERS_INVALID_EMAIL(false, 5000, "이메일 양식이 맞지 않습니다."),
    POST_USERS_INVALID_PWD(false, 5001, "비밀번호 양식이 맞지 않습니다."),
    POST_REVIEW_IMG_ERROR(false, 5001, "리뷰 이미지 에러입니다."),

    USER_PROFILE_EMPTY(false, 4000, "유효하지 않은 유저 프로필 인덱스입니다."),
    DUPLICATE_MEMBER_STATUS(false, 4000, "이미 그룹에 존재하는 유저 프로필입니다."),
    CLUB_EMPTY(false, 4000, "유효하지 않은 그룹 인덱스입니다."),
    DUPLICATE_TIMETABLE(false, 4000, "중복된 시간표입니다."),
    POST_MEMBER_STATUS_FAIL(false, 4004, "그룹 신청에 실패하였습니다"),
    MEMBER_STATUS_EMPTY(false, 4000, "신청된 그룹이 존재하지 않습니다."),
    POST_TIME_TABLE_FAIL(false, 4000, "시간표 등록에 실패하였습니다."),
    INVALID_MEMBER_STATUS(false, 4000, "유효하지 않은 member status 인덱스입니다."),

    SOCIAL(false, 5001, "소셜로 로그인을 진행한 이메일 입니다."),
    NOT_SOCIAL(false, 2001, "소셜이 로그인으로 진행한 이메일입니다."),

    VALIDATION_ERROR(false, 4000, "유효하지 않은 입력 값입니다."),
    /*
     * 5000: database error
     * */

    /*
     * 7000 : PATCH
     * */
    PATCH_PASSWORD_CHECK_WRONG(false, 7000, "비밀번호 확인란을 다시 확인해주세요."),
    PATCH_CLUB_ID_WRONG(false, 7001, "해당 그룹은 없는 그룹입니다."),
    PATCH_NOT_HOST(false, 7002, "해당 그룹의 방장이 아닙니다."),
    PATCH_HOST_DROPPED_INVALID(false, 7003, "방장은 강퇴할 수 없습니다."),

    /*
     * 8000 : delete
     * */

    /*
     * 9500 : jwt
     * */

    WRONG_JWT_SIGN_TOKEN(false, 9500, "잘못된 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(false, 9501, "만료된 JWT 토큰 입니다."),
    UNSUPPORTED_JWT_TOKEN(false, 9502, "지원되지 않는 JWT 토큰입니다."),
    WRONG_JWT_TOKEN(false, 9503, "JWT 토큰이 잘못되었습니다."),
    NULL_JWT(false,9504, "JWT의 값이 없습니다."),
    INVALID_JWT_TOKEN(false, 9505, "Refresh Token 이 유효하지 않습니다."),
    NOT_SAME_USER_INFO(false, 9506, "토큰의 유저 정보가 일치하지 않습니다."),
    LOGOUT_USER(false, 9507, "로그아웃된 사용자입니다.")
    ;


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
