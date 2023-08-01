package com.app.univchat.repository;

import com.app.univchat.domain.ClassChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassChatRoomRepository extends JpaRepository<ClassChatRoom, Long> {
    Optional<ClassChatRoom> findByClassNumber(String roomId);

}
