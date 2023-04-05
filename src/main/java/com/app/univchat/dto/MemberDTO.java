package com.app.univchat.dto;

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
    private String profileImgUrl;

//    private final Member member;

    @Builder
    public MemberDTO(String email, String password, String nickname, String gender, String profileImgUrl) {
        this.email=email;
        this.password=password;
        this.nickname=nickname;
        this.gender=gender;
        this.profileImgUrl=profileImgUrl;
    }

    // Dto를 entity 객체로 만들어 return해주는 메서드
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .profile_image_url(profileImgUrl)
                .build();
    }

//    public static MemberDTO from

}
