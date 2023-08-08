package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;
@Getter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "class_room_member")
public class ClassChatMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classRoomMemberId;

    @ManyToOne
    @JoinColumn(name = "classNumber", nullable = false)
    private ClassRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String lastAccessTime;
}