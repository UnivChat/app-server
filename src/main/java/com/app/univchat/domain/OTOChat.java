package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatting_one_to_one")
@ToString
public class OTOChat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    public OTOChatRoom room;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    public Member member; // 메시지 송신자 식별자

    @Column(nullable = false)
    private String messageContent;

    @Column(nullable = false)
    private String messageSendingTime;

}
