package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatting_live")
public class LiveChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    public Member member;

    @Column(nullable = false)
    private String messageContent;

    @Column(nullable = false)
    private String messageSendingTime;
}
