package com.app.univchat.controller;

import com.app.univchat.aws.AWSS3Uploader;
import com.app.univchat.base.BaseEntity;
import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/hello")
public class HelloController {

    private final AWSS3Uploader awss3Uploader;

    private final MemberService memberService;


    @GetMapping("/1")
    public BaseResponse<String> hello1() {
        return BaseResponse.ok(BaseResponseStatus.SUCCESS, "hello1");
    }

    @GetMapping("/2")
    public BaseResponse<String> hello2() {
        return BaseResponse.ok(BaseResponseStatus.SUCCESS, "hello2");
    }

    @PostMapping("/3")
    public BaseResponse<String> hello3(@RequestParam("image") MultipartFile imageFile) throws IOException {

        String imageUrl = awss3Uploader.uploadFiles(imageFile, "profile");

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, imageUrl); }

    @ApiOperation(value = "기본 응답, 에러 핸들링 예제", notes = "code 없이 보내면 200 Ok 반환 \n\n code = 400 - 400 Bad Request \n\n code = 404 - 404 Not Found")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 400, message = "요청에 실패했습니다."),
            @ApiResponse(code = 404, message = "해당 요청은 존재하지 않습니다.")
    })
    @GetMapping("/test/error")
    public ResponseEntity<BaseResponse> testError(@RequestParam(required = false) Long code){

        if (code != null && code == 400) {
            throw new BaseException(BaseResponseStatus.FAIL);
        } else if (code != null && code == 404) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(BaseResponse.ok(BaseResponseStatus.SUCCESS), HttpStatus.OK);

    }

}
