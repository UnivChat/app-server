package com.app.univchat.repository;

import com.app.univchat.domain.LoveChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoveChatRepository extends JpaRepository<LoveChat, Long> {
    Page<LoveChat> findAll(Pageable pageable);
}
