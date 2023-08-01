package com.app.univchat.domain;

import lombok.*;

import javax.persistence.*;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "class_room_member")
public class ClassChatRoomMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classRoomMemberId;

    @ManyToOne
    @JoinColumn(name = "classNumber", nullable = false)
    private ClassChatRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column
    private String lastAccessTime;
}
