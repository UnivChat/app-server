package com.app.univchat.repository;

import com.app.univchat.domain.LoveChat;
import com.app.univchat.domain.OTOChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTOChatRepository extends JpaRepository<OTOChat, Long> {
//    Page<OTOChat> findAll(Pageable pageable);
    Page<OTOChat> findByRoom(Long roomId, Pageable pageable);
}
