package com.app.univchat.controller;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;

import com.app.univchat.domain.Member;
import com.app.univchat.dto.JwtDto;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.MemberService;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.service.EmailService;
import com.app.univchat.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
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
    private final RedisService redisService;

    // 이메일 인증
    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "이메일 인증 API", notes = "이메일을 보내주시면 랜덤 범호 6자리를 반환")
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
//        if(memberService.checkEmail(memberDto.getEmail())) {
//            throw new BaseException(BaseResponseStatus.USER_ALREADY_EXIST_USERNAME);
//        }
        // 닉네임 중복 체크
//        if(memberService.checkNickname(memberDto.getNickname())) {
//            throw new BaseException(BaseResponseStatus.USER_EXISTS_NICKNAME_ERROR);
//        }

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,memberService.signup(memberDto));

    }

    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "닉네임 중복 확인 API")
    @GetMapping("/check/nickname")
    public BaseResponse<String> checkNickname(@RequestBody MemberReq.CheckNicknameReq checkNicknameReq){

        String checkRes=memberService.checkNickname(checkNicknameReq);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, checkRes);

    }

    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "이메일 중복 확인 API")
    @GetMapping("/check/email")
    public BaseResponse<String> checkEmail(@RequestBody MemberReq.CheckEmailReq checkEmailReq){

        String checkRes=memberService.checkEmail(checkEmailReq);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, checkRes);

    }


    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "비밀번호 변경 API")
    @PostMapping("/change/password")
    public BaseResponse<String> updatePassword(@RequestBody MemberReq.UpdatePasswordReq updatePasswordReq){

        memberService.updatePassword(updatePasswordReq);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS);

    }

    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "회원조회 API")
    @GetMapping("/info")
    public BaseResponse<MemberRes.InfoRes> viewInfo(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {
        MemberRes.InfoRes infoRes = memberService.viewInfo(member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, infoRes);
    }

    /**
     * 성별 조회 api
     */
    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "성별조회 API")
    @GetMapping("/gender")
    public BaseResponse<MemberRes.GenderRes> viewGender(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        MemberRes.GenderRes viewGender=memberService.viewGender(member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,viewGender);
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
    public BaseResponse<MemberRes.Update> update(@RequestPart MemberReq.Update memberUpdateDto,
                                    @RequestPart(required = false) MultipartFile profileImage,
                                    @ApiIgnore @AuthenticationPrincipal PrincipalDetails member){

        if (profileImage != null && !profileImage.isEmpty()) {
            memberUpdateDto.setProfileImage(profileImage);
        }

        MemberRes.Update result = memberService.update(memberUpdateDto, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }




    @Tag(name = "member", description = "액세스토큰 재발급 API")
    @ApiOperation(value = "액세스토큰 재발급 API")
    @PostMapping("/re-token")
    public BaseResponse<MemberRes.PostReIssueRes> reIssueToken(@RequestBody MemberReq.PostReIssueReq postReIssueReq) {

        //이메일(키)로 저장된 리프레시 토큰(밸류) 불러오기
        String redisRT = redisService.getValues(String.valueOf(postReIssueReq.getEmail()));

        if (redisRT == null) {
            throw new BaseException(BaseResponseStatus.JWT_INVALID_REFRESH_TOKEN);
        }
        if (!redisRT.equals(postReIssueReq.getRefreshToken())) {
            throw new BaseException(BaseResponseStatus.JWT_INVALID_USER_JWT);
        }

        MemberRes.PostReIssueRes postReIssueRes = memberService.reIssueToken(postReIssueReq.getEmail());

        return BaseResponse.ok(SUCCESS, postReIssueRes);
    }

    /**
     * 회원 탈퇴 api
     */
    @Tag(name = "member", description = "회원 관리 API")
    @ApiOperation(value = "회원탈퇴 API")
    @DeleteMapping("/delete")
    public BaseResponse<String> memberDelete(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        String deleteRes=memberService.memberDelete(member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,deleteRes);

    }

}
