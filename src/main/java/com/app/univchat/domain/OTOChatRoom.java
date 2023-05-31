package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "one_to_one_room")
@ToString
public class OTOChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    public Member sender; // 메시지 송신자 식별자

    @ManyToOne
    @JoinColumn(name = "receive_id", nullable = false)
    public Member receive; // 메시지 송신자 식별자

//    @Column(nullable = false)
//    private int visible;

//    @Column(nullable = false)
//    private Long lastMessageId;

}
