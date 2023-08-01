package com.app.univchat.repository;

import com.app.univchat.domain.ClassChatRoomMember;
import com.app.univchat.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassChatRoomMemberRepository extends JpaRepository<ClassChatRoomMember, Long> {
    List<ClassChatRoomMember> findByMember(Member member);

    void deleteByClassRoomClassNumberAndMember(String roomId, Member member);
}
