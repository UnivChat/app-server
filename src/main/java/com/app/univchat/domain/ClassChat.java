package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private ClassRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private Member member;
}