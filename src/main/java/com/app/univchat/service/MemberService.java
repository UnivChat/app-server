package com.app.univchat.service;

import com.app.univchat.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
//    private PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository=memberRepository;
    }

//    @Transactional
    public MemberDTO signup(MemberDTO memberDTO) throws BaseException{

        String rawPassword=memberDTO.getPassword();
        // password 암호화 - BCryptPasswordEncoder 를 객체로 받아옴
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword=passwordEncoder.encode(rawPassword);
        memberDTO.setPassword(encPassword);

        // dto를 entitiy로 변환해 저장
        memberRepository.save(memberDTO.toEntity());
        return memberDTO;
    }

    public boolean checkEmail(String email) {
        return memberRepository.checkEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.checkNickname(nickname);
    }
}
