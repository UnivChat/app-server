package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;

import com.app.univchat.domain.Member;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.MemberService;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.service.EmailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;

import static com.app.univchat.base.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final EmailService emailService;
    private final MemberService memberService;

    // 이메일 인증
    @SneakyThrows
    @PostMapping("/email/verified")
    public BaseResponse<MemberRes.EmailAuthRes> memberEmailVerified(@RequestBody MemberReq.EmailAuthReq emailAuthReq) {
        MemberRes.EmailAuthRes emailAuthRes = emailService.sendEmail(emailAuthReq.getEmail());

        return BaseResponse.ok(SUCCESS, emailAuthRes);
    }


    // 회원가입
    @ApiOperation(value = "회원가입 API", notes = "이메일 형식을 보내주세요.")
    @PostMapping("/signup")
    public BaseResponse<String> signup(@RequestBody MemberReq.Signup memberDto){

        // 이메일 중복 체크
        if(memberService.checkEmail(memberDto.getEmail())) {
            return BaseResponse.ok(BaseResponseStatus.USER_ALREADY_EXIST_USERNAME);
        }
        // 닉네임 중복 체크
        if(memberService.checkNickname(memberDto.getNickname())) {
            return BaseResponse.ok(BaseResponseStatus.USER_EXISTS_NICKNAME_ERROR);
        }

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,memberService.signup(memberDto));

    }

    // 비밀번호 변경
    @ApiOperation(value = "비밀번호 변경 API")
    @PostMapping("/change/password")
    public BaseResponse<String> updatePassword(@RequestBody MemberReq.UpdatePasswordReq updatePasswordReq){

        String passwordRes=memberService.updatePassword(updatePasswordReq);

        if(passwordRes!=null) {
            return BaseResponse.ok(BaseResponseStatus.SUCCESS,passwordRes);
        }
        else {
            return BaseResponse.ok(BaseResponseStatus.USER_NOT_EXIST_EMAIL_ERROR);
        }

    }

    @ApiOperation(value = "회원조회 API")
    @GetMapping("/info")
    public BaseResponse<MemberRes.InfoRes> viewInfo(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        MemberRes.InfoRes infoRes=memberService.viewInfo(member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,infoRes);
    }

}
