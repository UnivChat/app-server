package com.app.univchat.repository;

import com.app.univchat.domain.ClassChat;
import com.app.univchat.domain.ClassChatMember;
import com.app.univchat.domain.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface ClassChatRepository extends JpaRepository<ClassChat, Long> {
    List<ClassChat> findByClassRoom(ClassRoom classRoom);

    Page<ClassChat> findByClassRoom(ClassRoom classRoom, Pageable pageable);

    Optional<ClassChat> findTopByClassRoomOrderByMessageSendingTimeDesc(ClassRoom classRoom);
    int countByMessageSendingTimeGreaterThan(String messageSendingTime);
}
