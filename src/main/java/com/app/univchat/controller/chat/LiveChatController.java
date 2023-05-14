package com.app.univchat.controller.chat;

import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Tag(name = "chatting", description = "채팅 내역 조회 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/live")
public class LiveChatController {

    @MessageMapping("/live")
    @SendTo("/sub/live")
    public ChatRes.LiveChatRes sendToDormChattingRoom(ChatReq.DormChatReq liveChatReq) {
        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());

        return new ChatRes.LiveChatRes().builder()
                .memberNickname(liveChatReq.getMemberNickname())
                .messageContent(liveChatReq.getMessageContent())
                .messageSendingTime(messageSendingTime)
                .build();
    }


}
