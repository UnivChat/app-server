package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm")
@ToString
public class Fcm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 메시지 송신자 식별자

    @Column(nullable = false)
    private String token;   // firebase device token
}

