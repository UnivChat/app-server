package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "chatting_class")
public class ClassChat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(nullable = false)
    private String messageSendingTime;

    @ManyToOne
    @JoinColumn(name = "classNumber", nullable = false)
    private ClassChatRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private Member member;
}
