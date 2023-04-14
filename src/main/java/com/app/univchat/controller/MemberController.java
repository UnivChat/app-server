package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.MemberService;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.service.EmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

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

    /**
     * 회원 수정 api
     */
    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "회원 수정 API",
            notes = "Content-type: form-data \n\n Dto 데이터 Content-type: application/json")
    @PutMapping("/info")
    public ResponseEntity<?> update(@RequestPart MemberReq.Update memberUpdateDto,
                                    @RequestPart(required = false) MultipartFile profileImage,
                                    @ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        if (profileImage != null && !profileImage.isEmpty()) {
            memberUpdateDto.setProfileImage(profileImage);
        }

        memberService.update(memberUpdateDto, member.getMember());

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS));
    }

}
