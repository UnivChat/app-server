package com.app.univchat.service;

import com.app.univchat.aws.AWSS3Uploader;
import com.app.univchat.base.BaseException;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     *
     * @return
     */
    @Transactional
    public MemberRes.Update update(MemberReq.Update memberUpdateDto, Member member){
        //업데이트
        member.updateNickname(memberUpdateDto.getNickname());

        //프로필 사진 업데이트
        if (memberUpdateDto.getProfileImage() != null && !memberUpdateDto.getProfileImage().isEmpty()) {
            updateProfileImage(memberUpdateDto.getProfileImage(), member);
        }

        memberRepository.save(member);

        return new MemberRes.Update(member.getNickname(), member.getProfileImageUrl());
    }

    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @SneakyThrows(IOException.class)
    public void updateProfileImage(MultipartFile image, Member member) {
        //기존 프로필 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null) {
            awss3Uploader.deleteByUrl(member.getProfileImageUrl());
        }

        //프로필 이미지 업로드
        String url = awss3Uploader.uploadFiles(image, "profile");
        member.updateProfileImage(url);
    }
}
