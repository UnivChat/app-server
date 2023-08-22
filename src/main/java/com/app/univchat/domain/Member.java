package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class Member { 
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String gender;

    private String profileImageUrl;

    @Column(name = "firebase_token")
    private String firebaseToken;


    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updatePassword(String password) {
        this.password=password;
    }

    public void updateFCMToken(String firebaseToken) {
        this.firebaseToken=firebaseToken;
    }

}
