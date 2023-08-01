package com.app.univchat.repository;

import com.app.univchat.domain.ClassChat;
import com.app.univchat.domain.ClassChatRoom;
import com.app.univchat.domain.OTOChat;
import com.app.univchat.domain.OTOChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassChatRepository extends JpaRepository<ClassChat, Long> {
    List<ClassChat> findByClassRoom(ClassChatRoom classRoom);

    Page<ClassChat> findByClassRoom(ClassChatRoom classRoom, Pageable pageable);
}
