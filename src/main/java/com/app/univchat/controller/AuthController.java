package com.app.univchat.controller;

import com.app.univchat.base.BaseEntity;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.LoginDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "member", description = "회원 관리 API")
public class AuthController {




    @Tag(name = "member")
    @ApiOperation(value = "로그인 API")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청에 성공하였습니다.", response = LoginDto.Res.class)
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginDto.Res>> login(@RequestBody LoginDto.Req loginDto){

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, new LoginDto.Res()));
    }


}
