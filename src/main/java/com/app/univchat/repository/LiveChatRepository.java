package com.app.univchat.repository;

import com.app.univchat.domain.LiveChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveChatRepository extends JpaRepository<LiveChat, Long> {

    Page<LiveChat> findAll(Pageable pageable);
}
