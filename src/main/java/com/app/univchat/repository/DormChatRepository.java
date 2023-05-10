package com.app.univchat.repository;

import com.app.univchat.domain.DormChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DormChatRepository extends JpaRepository<DormChat, Long> {
    Page<DormChat> findAll(Pageable pageable);
}
