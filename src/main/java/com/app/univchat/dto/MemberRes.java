package com.app.univchat.dto;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class MemberRes {


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class EmailAuthRes {
        int checkCode;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "회원 수정 api 응답 객체")
    public static class Update {
        @NotNull
        @ApiModelProperty(name = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(name = "프로필 이미지 url", example = "https://~~~~~~~~~.jpg")
        private String profileImageUrl;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "회원 조회 api 응답 객체")
    public static class InfoRes {
        String email;
        String nickname;
        String gender;
        String profileImgUrl;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "성별 조회 api 응답 객체")
    public static class GenderRes {
        String gender;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "PostReIssueRes - 리프레시토큰으로 액세스토큰 재발급")
    public static class PostReIssueRes {
        private String email;
        private JwtDto jwtDto;
    }


}
