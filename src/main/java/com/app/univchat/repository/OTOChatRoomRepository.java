package com.app.univchat.repository;

import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface OTOChatRoomRepository extends JpaRepository<OTOChatRoom, Long> {
    // 송신자, 수신자로 채팅방 찾기
    Optional<OTOChatRoom> findBySenderAndReceive(Member sender, Member receive);

    // 사용자로 채팅방 찾기
    // TODO: 같은 값인데, 1개로 할 수는 없을까?
    List<OTOChatRoom> findBySenderOrReceive(Member member1, Member member2);

    boolean existsBySenderAndReceive(Member sender, Member receive);

    @Transactional
    void deleteByRoomId(Long roomId);

    // roomId로 채팅방 찾기
    Optional<OTOChatRoom> findByRoomId(Long roomId);

}
