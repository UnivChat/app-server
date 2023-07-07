package com.app.univchat.controller;


import com.app.univchat.dto.FCMRequestDto;
import com.app.univchat.service.FCMService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fcm")
public class FCMController {

    private final FCMService fcmService;

    @Tag(name = "fcm", description = "")
    @ApiOperation(value = "푸시 알림 API", notes = "푸시 알림")
    @PostMapping()
    public String sendNotificationByToken(@RequestBody FCMRequestDto requestDto) {
        return fcmService.sendNotificationByToken(requestDto);
    }
}