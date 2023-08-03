package com.app.univchat.repository;

import com.app.univchat.domain.ClassChatMember;
import com.app.univchat.domain.ClassRoom;
import com.app.univchat.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassChatMemberRepository extends JpaRepository<ClassChatMember, Long> {
    List<ClassChatMember> findByMember(Member member);

    Optional<ClassChatMember> findByClassRoomClassNumberAndMember(String classNumber, Member member);

    void deleteByClassRoomClassNumberAndMember(String classNumber, Member member);
}
