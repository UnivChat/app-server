package com.app.univchat.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 에러 코드 관리
 */
@AllArgsConstructor
@Getter
public enum  BaseResponseStatus {
    //모듈_에러내용_ERROR


    /**
     * 1000: 기본 요청 성공, 실패
     */
    SUCCESS("1000", "요청에 성공하였습니다.", 200),

    FAIL("1001", "요청에 실패했습니다.", 400),

    NOT_FOUND("1002", "해당 요청은 존재하지 않습니다.", 404),


    /**
     * 2000 : User 오류
     */
    USER_EXISTS_NICKNAME_ERROR("2001","중복된 닉네임입니다.", 409),

    USER_FAILED_TO_LOG_IN_ERROR("2002", "로그인에 실패하였습니다.", 400),

    USER_NOT_EXIST_USER_IDX_ERROR("2003", "존재하지 않는 유저 idx입니다.", 404),

    USER_NOT_EXIST_NICKNAME_ERROR("2004", "존재하지 않는 닉네임입니다.", 404),

    USER_NOT_EXIST_EMAIL_ERROR("2005", "존재하지 않는 이메일입니다.", 404),

    USER_ALREADY_DELETED_ERROR("2006", "이미 탈퇴된 유저입니다.", 400),

    USER_NOT_EXIST_PROVIDER_ERROR("2007", "제공하지 않는 소셜 로그인입니다.", 400),

    USER_ERROR("2008", "로그인된 유저 찾기 실패", 400),

    USER_FAILED_TO_SET_PASSWORD("2009", "비밀번호 재설정에 실패했습니다.", 400),

    USER_FAILED_TO_SET_PICTURE("2010", "프로필 사진 설정에 실패했습니다.", 400),

    USER_ALREADY_EXIST_USERNAME("2011", "이미 존재하는 이메일입니다.", 400),

    USER_NOT_CORRECT_PASSWORD("2012", "틀린 비밀번호입니다. 다시 입력해주세요.", 400),

    USER_EMAIL_VERIFIED_FALSE("2013", "이메일 인증에 실패했습니다", 400),

    USER_NEED_TO_LOGIN("2014", "로그인 후에 접근해주시길 바랍니다", 400),

    USER_FAILED_TO_WITHDRAWAL("2014", "회원 탈퇴에 실패하였습니다", 400),





    /**
     * 6000 : Jwt 오류
     */
    JWT_UNKNOWN_ERROR("6001", "JWT토큰을 입력해주세요.", 400),

    JWT_WRONG_TYPE_TOKEN("6002", "잘못된 JWT 서명입니다.", 400),

    JWT_EXPIRED_TOKEN("6003", "만료된 토큰입니다.", 400),

    JWT_UNSUPPORTED_TOKEN("6004", "지원되지 않는 토큰입니다.", 400),

    JWT_ACCESS_DENIED("6005","접근이 거부되었습니다.", 400),

    JWT_WRONG_TOKEN("6006","JWT 토큰이 잘못되었습니다.", 400),

    JWT_HIJACK_ACCESS_TOKEN("6007", "만료된(탈취된) 서명입니다.", 400),
    JWT_INVALID_REFRESH_TOKEN("6008", "유효하지 않은 리프레시 토큰입니다.", 400),
    JWT_INVALID_USER_JWT("6008", "입력 RT와 사용자 RT가 일치하지 않습니다", 400),
    JWT_AND_NICKNAME_DONT_MATCH("6009", "JWT와 닉네임이 일치하지 않습니다.", 400),
    ;

    private final String code;
    private final String message;
    private final int status;


}
