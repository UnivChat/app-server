package com.app.univchat.dto;

import com.app.univchat.domain.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDTO {
    private long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    @NotNull
    private String gender;
    private String profileImageUrl;

//    private final Member member;

    @Builder
    public MemberDTO(String email, String password, String nickname, String gender, String profileImageUrl) {
        this.email=email;
        this.password=password;
        this.nickname=nickname;
        this.gender=gender;
        this.profileImageUrl=profileImageUrl;
    }

    // Dto를 entity 객체로 만들어 return해주는 메서드
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .profileImageUrl(profileImageUrl)
                .build();
    }

//    public static MemberDTO from

}
