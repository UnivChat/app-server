package com.app.univchat.service;

import com.app.univchat.dto.MemberJoinDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

public class MemberJoinService {
    @Autowired
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    public MemberJoinService() {
    }

    @Transactional
    public boolean join(MemberJoinDTO memberJoinDTO) {
        String rawPassword=memberJoinDTO.getPassword();
        // password를 암호화
        String encPassword=passwordEncoder.encode(rawPassword);

        memberJoinDTO.setPassword(encPassword);
        memberRepository.save(memberJoinDTO.toEntity());
        return true;
    }
}
