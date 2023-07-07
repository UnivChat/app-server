package com.app.univchat.repository;

import com.app.univchat.domain.Fcm;
import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Optional<Fcm> findByMember(Member member);

}
