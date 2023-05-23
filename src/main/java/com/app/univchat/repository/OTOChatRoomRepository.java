package com.app.univchat.repository;

import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTOChatRoomRepository extends JpaRepository<OTOChatRoom, Long> {
//    Page<OTOChatRoom> findAll(Pageable pageable);

    // 송신자 존재 여부 확인
//    boolean existsBySender(Long id);

    // 수신자 존재 여부 확인
//    boolean existsBySenderAndReceive(Long sender, Long receive);

    // 송신자, 수신자로 채팅방 찾기
    Optional<OTOChatRoom> findBySenderAndReceive(Member sender, Member receive);

    boolean existsBySenderAndReceive(Member sender, Member receive);

    // roomId로 채팅방 찾기
    Optional<OTOChatRoom> findByRoomId(Long roomId);

}
