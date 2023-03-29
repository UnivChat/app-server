package com.app.univchat.service;

import com.app.univchat.dto.MemberJoinDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class MemberJoinService {
    @Autowired
    private MemberRepository memberRepository;
//    private PasswordEncoder passwordEncoder;

//    public MemberJoinService() {
//    }

//    @Transactional
    public boolean join(MemberJoinDTO memberJoinDTO) {
        String rawPassword=memberJoinDTO.getPassword();
        // password 암호화 - BCryptPasswordEncoder 를 객체로 받아옴
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword=passwordEncoder.encode(rawPassword);
        memberJoinDTO.setPassword(encPassword);

        // dto를 entitiy로 변환해 저장
        memberRepository.save(memberJoinDTO.toEntity());
        return true;
    }
}
