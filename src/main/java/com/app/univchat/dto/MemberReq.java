package com.app.univchat.dto;

import com.app.univchat.domain.Member;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class MemberReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class EmailAuthReq {
        private String email;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class Signup {
        @NotNull
        private String email;
        @NotNull
        private String password;
        @NotNull
        private String nickname;
        @NotNull
        private String gender;

        // Dto를 entity 객체로 만들어 return해주는 메서드
        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .gender(gender)
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "memberUpdateDto - 회원 수정 api 요청 객체")
    public static class Update {
        @NotNull
        @ApiModelProperty(name = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(hidden = true)
        private MultipartFile profileImage;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "UpdatePasswordReq - 비밀번호 변경 요청 객체")
    public static class UpdatePasswordReq {
        private String email;
        private String password;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "PostReIssueReq - 리프레시토큰으로 액세스토큰 재발급")
    public static class PostReIssueReq {
        private String email;
        private String refreshToken;
    }
}
