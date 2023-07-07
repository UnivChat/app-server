package com.app.univchat.repository;

import com.app.univchat.domain.Fcm;
import com.app.univchat.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Optional<Fcm> findByMember(Member member);

}
