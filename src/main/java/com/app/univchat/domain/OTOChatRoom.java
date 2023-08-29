package com.app.univchat.domain;

import com.app.univchat.chat.OTOChatVisible;
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
    @JoinColumn(name = "receiver_id", nullable = false)
    public Member receiver; // 메시지 송신자 식별자

    @Column(columnDefinition="ENUM('ALL','SENDER','RECEIVER')" ,nullable = false )
    @Enumerated(EnumType.STRING)
    private OTOChatVisible visible;

//    @Column(nullable = false)
//    private Long lastMessageId;

    public void updateVisible(OTOChatVisible visible) {
        this.visible = visible;
    }

    public void exitAllChatRoom() {
        System.out.println("채팅방 나가기");
    }

}
