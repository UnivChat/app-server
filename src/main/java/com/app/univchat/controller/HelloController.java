package com.app.univchat.controller;

import com.app.univchat.aws.AWSS3Uploader;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/hello")
public class HelloController {

    private final AWSS3Uploader awss3Uploader;

    public HelloController(AWSS3Uploader awss3Uploader) {
        this.awss3Uploader = awss3Uploader;
    }

    @GetMapping("/1")
    public ResponseEntity<String> hello1() {
        return ResponseEntity.ok("hello1");
    }

    @GetMapping("/2")
    public BaseResponse<String> hello2() {
        return BaseResponse.ok(BaseResponseStatus.SUCCESS, "hello2");
    }

    @PostMapping("/3")
    public BaseResponse<String> hello3(@RequestParam("image") MultipartFile imageFile) throws IOException {

        String imageUrl = awss3Uploader.uploadFiles(imageFile, "profile");

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, imageUrl); }

}
