package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.service.DormChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@MessageMapping("/dorm")
@RequestMapping("/dorm")
public class DormChatController {

    private final DormChatService dormChatService;

    // 기숙사 채팅 송신 및 저장을 위한 API(ws)
    @MessageMapping("/{roomId}") // 기숙사 식별자
    @SendTo("/sub/dorm/{roomId}")
    public ChatRes.DormChatRes sendToDormChattingRoom(ChatReq.DormChatReq dormChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        dormChatService.saveChat(dormChatReq, messageSendingTime);

        return new ChatRes.DormChatRes().builder()
                .memberNickname(dormChatReq.getMemberNickname())
                .messageContent(dormChatReq.getMessageContent())
                .messageSendingTime(messageSendingTime)
                .build();
    }

    // 기숙사 채팅 내역을 불러오기 위한 API(http)
    @GetMapping("/chat/{roomId}/{page}")
    public ResponseEntity<BaseResponse<List<ChatRes.DormChatRes>>>loadDormChattingList(@PathVariable Long roomId, @PathVariable int page) {

        List<ChatRes.DormChatRes> chattingList = dormChatService.getChattingList(roomId, page);

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList));
    }
}
