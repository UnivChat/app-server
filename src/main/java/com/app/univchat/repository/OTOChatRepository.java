package com.app.univchat.repository;

import com.app.univchat.domain.OTOChat;
import com.app.univchat.domain.OTOChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTOChatRepository extends JpaRepository<OTOChat, Long> {
    Page<OTOChat> findAll(Pageable pageable);
    Page<OTOChat> findByRoom(Optional<OTOChatRoom> room, Pageable pageable);

    OTOChat findTop1ByRoomOrderByMessageSendingTimeDesc(OTOChatRoom room);
}
