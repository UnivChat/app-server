package com.app.univchat.service;

import com.app.univchat.base.BaseException;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository=memberRepository;
    }

//    @Transactional
    public String signup(MemberReq.Signup memberDto) throws BaseException {

        String rawPassword=memberDto.getPassword();
        // password 암호화 - BCryptPasswordEncoder 를 객체로 받아옴
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword=passwordEncoder.encode(rawPassword);
        memberDto.setPassword(encPassword);

        // dto를 entitiy로 변환해 저장
        memberRepository.save(memberDto.toEntity());
        return null;
    }

    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
