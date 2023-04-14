package com.app.univchat.service;

import com.app.univchat.aws.AWSS3Uploader;
import com.app.univchat.base.BaseException;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AWSS3Uploader awss3Uploader;

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository=memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional
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

    /**
     * 회원 수정
     */
    @Transactional
    public void update(MemberReq.Update memberUpdateDto, Member member) throws IOException {
        //업데이트
        member.updateNickname(memberUpdateDto.getNickname());

        //TODO: 기존의 프로필 사진 있을 경우 삭제 필요
        //프로필 사진 업데이트
        if (memberUpdateDto.getProfileImage() != null && !memberUpdateDto.getProfileImage().isEmpty()) {
            String url = awss3Uploader.uploadFiles(memberUpdateDto.getProfileImage(), "member");
            member.updateProfileImage(url);
        }

        memberRepository.save(member);
    }

    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
