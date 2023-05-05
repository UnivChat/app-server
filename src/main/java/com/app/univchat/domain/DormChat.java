package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatting_dorm")
@ToString
public class DormChat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private Long roomId; // 기숙사 식별자

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    public Member member; // 메시지 송신자 식별자

    @Column(nullable = false)
    private String messageContent;

    @Column(nullable = false)
    private String messageSendingTime;

}
