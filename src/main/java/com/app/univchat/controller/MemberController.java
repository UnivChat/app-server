package com.app.univchat.controller;

import com.app.univchat.dto.MemberDTO;
import com.app.univchat.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    private  MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public BaseResponse<MemberDTO> signup(MemberDTO memberDTO) throws  BaseException{
        try {
            // 이메일 중복 체크
            if(memberService.checkEmail(memberDTO.getEmail())) {
                return new BaseResponse<>(USER_ALREADY_EXIST_USERNAME);
            }
            // 닉네임 중복 체크
            if(memberService.checkNickname(memberDTO.getNickname())) {
                return new BaseResponse<>(USER_EXISTS_NICKNAME_ERROR);
            }

            return new BaseResponse<>(memberService.signup(memberDTO));
        } catch(BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }

    }
}
