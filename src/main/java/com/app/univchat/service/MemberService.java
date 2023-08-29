package com.app.univchat.service;

import com.app.univchat.aws.AWSS3Uploader;
import com.app.univchat.base.BaseException;

import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.chat.OTOChatVisible;
import com.app.univchat.config.SecurityUtil;

import com.app.univchat.domain.Member;
import com.app.univchat.dto.JwtDto;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.repository.MemberRepository;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.chat.OTOChatService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AWSS3Uploader awss3Uploader;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;


    @Value("${jwt.expire-time.refresh-token}")
    private int refreshTime;

    // 현재 유저 반환
    public Member findNowUser() {
        return SecurityUtil.getCurrentMemberId().flatMap(memberRepository::findByEmail).orElse(null);
    }

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

    /**
     * 회원 조회
     */
    public MemberRes.InfoRes viewInfo(Member member) throws IOException{

        MemberRes.InfoRes infoRes = new MemberRes.InfoRes();

        infoRes.setEmail(member.getEmail());
        infoRes.setNickname(member.getNickname());
        infoRes.setGender(member.getGender());
        if(member.getProfileImageUrl()==null) {
            infoRes.setProfileImgUrl(" ");
        }
        else infoRes.setProfileImgUrl(member.getProfileImageUrl());

        return infoRes;
    }

    /**
     * nickname을 통한 member 조회
     */
    public Optional<Member> getMember(String nickname) throws IOException{

        return memberRepository.findByNickname(nickname);
    }

    /**
     * email을 통한 member 조회
     */
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * 성별 조회
     */
    public MemberRes.GenderRes viewGender(Member member) throws IOException{

        MemberRes.GenderRes genderRes=new MemberRes.GenderRes();
        genderRes.setGender(member.getGender());

        return genderRes;
    }

    /**
     * 회원 탈퇴
     */
    public String memberDelete(Member member) throws IOException{

        /** hard delete **/
//        Long id=member.getId();
//        memberRepository.deleteById(id);

        /** soft delete **/
        //프로필 이미지 삭제
        if (member.getProfileImageUrl() != null) {
            awss3Uploader.deleteByUrl(member.getProfileImageUrl());
            member.updateProfileImage(null);
        }

        // 참여중인 1:1 채팅방 퇴장기

//        otoChatService.exitAllChatRoom(member);

        //firebase token, 닉네임, 성별 삭제
        member.updateFCMToken(null);
        member.deleteMember();

        //로그아웃
        memberLogout(member);

        memberRepository.save(member);

        return "회원 탈퇴가 완료되었습니다.";
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(MemberReq.UpdatePasswordReq updatePasswordReq) throws BaseException {

        Optional<Member> foundMember=memberRepository.findByEmail(updatePasswordReq.getEmail());
        Member member = foundMember.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_EMAIL_ERROR));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword=passwordEncoder.encode(updatePasswordReq.getPassword());
        member.updatePassword(encPassword);
        memberRepository.save(member);

    }

    public String UpdateFCM(MemberReq.UpdateFCMReq updateFCMReq, Member member) throws BaseException {

        member.updateFCMToken(updateFCMReq.getFcmToken());
        memberRepository.save(member);

        return "firebase token이 등록되었습니다.";
    }

    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean checkDeletedMember(String nickname) {
        // 현재 참여 채팅방 객체
        Optional<Member> member=memberRepository.findByNickname(nickname);

        // 모든 유저가 채팅방에 남아있을 경우 true

        if(member.get().isWithdrawal())
            return true;
        else
            return false;
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

    public MemberRes.PostReIssueRes reIssueToken(String email) {
        Member member = memberRepository.findByEmail(email).get();
        JwtDto jwtDto = jwtProvider.generateToken(member.getEmail());


        //Redis 에 RefreshToken 저장
        redisService.saveToken(String.valueOf(email), jwtDto.getRefreshToken(),refreshTime);

        return new MemberRes.PostReIssueRes(member.getEmail(), jwtDto);
    }


    public String memberLogout(Member member) {

        String accessToken = jwtProvider.getJwt();
        System.out.println("현재 로그아웃 유저 액세스 토큰: " + accessToken);

        //유저의 리프레시 토큰 삭제
        String userRefreshToken = redisService.getValues(member.getEmail());
        System.out.println("현재 로그아웃 유저 리프레시 토큰: "+ userRefreshToken);
        redisService.deleteValues(member.getEmail());

        //유저의 액세스 토큰 블랙리스트에 등록
        //redisService.setBlackList(accessToken, "accessToken", 5);
        redisService.setValues(accessToken, "blackList");

        return "로그아웃 완료";
    }
}
