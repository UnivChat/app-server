package com.app.univchat.controller;

import com.app.univchat.dto.MemberJoinDTO;
import com.app.univchat.service.MemberJoinService;
import org.springframework.web.bind.annotation.PostMapping;

public class MemberJoinController {
    private MemberJoinService memberJoinService;

    // 회원가입
    @PostMapping("/member/signup")
    public String Signup(MemberJoinDTO memberJoinDTO) {
        memberJoinService.join(memberJoinDTO);
        return "/member/signup";    // 회원가입 완료시 결과로 수정
    }
}
