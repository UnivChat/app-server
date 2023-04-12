package com.app.univchat.dto;

import com.app.univchat.domain.Member;
import com.sun.istack.NotNull;
import lombok.*;

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
}
