package com.app.univchat.controller;

import com.app.univchat.base.BaseEntity;
import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.MemberDTO;
import com.app.univchat.repository.MemberRepository;
import com.app.univchat.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.app.univchat.base.BaseResponseStatus.*;

//@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
//    @Autowired
    private final MemberService memberService;
//    private final MemberRepository memberRepository;

    public MemberController(MemberService memberService) {
        this.memberService=memberService;
    }

    // 회원가입

    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody MemberDTO memberDTO) throws BaseException {
        try {
            // 이메일 중복 체크
            if(memberService.checkEmail(memberDTO.getEmail())) {
                return BaseResponse.ok(BaseResponseStatus.USER_ALREADY_EXIST_USERNAME);
            }
            // 닉네임 중복 체크
            if(memberService.checkNickname(memberDTO.getNickname())) {
                return BaseResponse.ok(BaseResponseStatus.USER_EXISTS_NICKNAME_ERROR);
            }

//            MemberDTO responseDTO=memberService.signup(memberDTO);
            return BaseResponse.ok(BaseResponseStatus.SUCCESS,memberService.signup(memberDTO));
        } catch(BaseException e) {

            return BaseResponse.ok(e.getStatus());
        }


    }

//    @GetMapping("/1")
//    public ResponseEntity<String> memberTest() {
//        return ResponseEntity.ok("memberTest");
//    }

}
