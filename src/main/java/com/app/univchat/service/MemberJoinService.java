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
    /*
        join의 반환형 확정 필요 -> dto를 성공적으로 저장했을 때, entity로 반환할 것인지, true로 성공여부만 전달할 것인지
     */
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
