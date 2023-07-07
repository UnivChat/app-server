package com.app.univchat.controller;


import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.FcmReq;
import com.app.univchat.service.FCMNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name="Notification",description="FCM Notification api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class FCMNotificationController {
    private final FCMNotificationService fcmNotificationService;

    @Operation(summary = "알림 보내기")
    @PostMapping("")
    public BaseResponse<String> sendNotification(@RequestBody FcmReq.Notification fcmNotificationReq) {

        String res=fcmNotificationService.sendNotificationByToken(fcmNotificationReq);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, res);
    }
}
