package com.example.relayRun.util;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
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
    /*
     * 4000: [POST]
     * */
    POST_USERS_EMPTY(false, 4777, "공백 없이 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 5000, "이메일 양식이 맞지 않습니다."),
    POST_USERS_INVALID_PWD(false, 5001, "비밀번호 양식이 맞지 않습니다."),
    POST_REVIEW_IMG_ERROR(false, 5001, "리뷰 이미지 에러입니다."),

    SOCIAL(false, 5001, "소셜로 로그인을 진행한 이메일 입니다."),
    NOT_SOCIAL(false, 2001, "소셜이 로그인으로 진행한 이메일입니다."),
    /*
     * 5000: database error
     * */
    PASSWORD_ENCRYPTION_ERROR(false, 4001, "비밀번호 암호화에 실패했습니다."),
    DATABASE_ERROR(false, 4002, "데이터베이스 연결에 실패하였습니다."),
    /*
     * 7000 : PATCH
     * */

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
