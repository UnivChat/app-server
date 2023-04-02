package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/1")
    public ResponseEntity<String> hello1() {
        return ResponseEntity.ok("hello1");
    }

    @GetMapping("/2")
    public BaseResponse<String> hello2() {
        return BaseResponse.ok(HttpStatus.OK, BaseResponseStatus.SUCCESS, "hello2");
    }


}
