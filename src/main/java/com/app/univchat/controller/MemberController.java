package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;

import com.app.univchat.domain.Member;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.MemberService;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
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
@Tag(name = "member", description = "회원 관리 API")
public class MemberController {

    private final EmailService emailService;
    private final MemberService memberService;

    // 이메일 인증
    @Tag(name = "member", description = "회원 관리 API")
    @SneakyThrows
    @PostMapping("/email/verified")
    public BaseResponse<MemberRes.EmailAuthRes> memberEmailVerified(@RequestBody MemberReq.EmailAuthReq emailAuthReq) {
        MemberRes.EmailAuthRes emailAuthRes = emailService.sendEmail(emailAuthReq.getEmail());

        return BaseResponse.ok(SUCCESS, emailAuthRes);
    }


    // 회원가입
    @Tag(name = "member", description = "회원 관리 API")
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

    /**
     * 회원 수정 api
     */
    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "회원 수정 API",
            notes = "form-data 형식으로 보내주세요.(다른 타입은 415 오류 발생) \n\n Dto - Content-type: application/json (명시하지 않을 시 오류 발생)" )
    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = MemberRes.Update.class),
            @ApiResponse(code = 415, message = "Content type 'application/octet-stream' not supported")
    })
    @PutMapping("/info")
    public ResponseEntity<BaseResponse<MemberRes.Update>> update(@RequestPart MemberReq.Update memberUpdateDto,
                                    @RequestPart(required = false) MultipartFile profileImage,
                                    @ApiIgnore @AuthenticationPrincipal PrincipalDetails member){

        if (profileImage != null && !profileImage.isEmpty()) {
            memberUpdateDto.setProfileImage(profileImage);
        }

        MemberRes.Update result = memberService.update(memberUpdateDto, member.getMember());

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, result));
    }

}
