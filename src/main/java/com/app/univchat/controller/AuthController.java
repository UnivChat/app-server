package com.app.univchat.controller;

import com.app.univchat.base.BaseEntity;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.LoginDto;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.MemberService;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "member", description = "회원 관리 API")
public class AuthController {

    private final MemberService memberService;


    @Tag(name = "member")
    @ApiOperation(value = "로그인 API")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청에 성공하였습니다.", response = LoginDto.Res.class)
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginDto.Res>> login(@RequestBody LoginDto.Req loginDto){

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, new LoginDto.Res()));
    }




    /**
     * 회원 로그아웃 api
     */
    @Tag(name = "member", description = "로그아웃 API")
    @ApiOperation(value = "로그아웃 API")
    @GetMapping("/member/logout")  //TODO 경로를 /logout이라고 하면 계속 오류가 발생해서 일단은 /member/logout로 설정했습니다ㅠㅠ!
    public BaseResponse<Object> logout(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        String result = memberService.memberLogout(member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

}
