package com.app.univchat.repository;

import com.app.univchat.domain.DormChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface DormChatRepository extends JpaRepository<DormChat, Long> {
    List<DormChat> findByRoomIdOrderByMessageSendingTimeDesc(Long roomId, Pageable pageable);
}
