package com.app.univchat.dto;

import com.app.univchat.domain.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public class LoginDto {

    @Data
    @ApiModel(value = "로그인 요청 DTO")
    public static class Req{

        @ApiModelProperty(name = "로그인 이메일", example = "aaa@catholic.ac.kr")
        private String email;

        @ApiModelProperty(name = "비밀번호", example = "1234")
        private String password;

    }

    @Data
    @ApiModel(value = "로그인 성공 반환 DTO")
    public static class Res{

        @ApiModelProperty(name = "로그인 이메일", example = "aaa@catholic.ac.kr")
        private String email;

        @ApiModelProperty(name = "닉네임", example = "닉네임1")
        private String nickname;

        @ApiModelProperty(name = "성별", example = "남")
        private String gender;

        @ApiModelProperty(name = "토큰")
        private JwtDto jwtDto;

        public Res(Member member, JwtDto jwtDto) {
            this.email = member.getEmail();
            this.nickname = member.getNickname();
            this.gender = member.getGender();
            this.jwtDto = jwtDto;
        }
    }

}
