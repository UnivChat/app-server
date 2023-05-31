package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.service.chat.LoveChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static com.app.univchat.base.BaseResponseStatus.CHAT_OVERFLOW_THE_RANGE;

@Tag(name = "chatting", description = "채팅 내역 조회 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/chatting/love")
public class LoveChatController {

    private final LoveChatService loveChatService;

    // 연애상담 채팅 송신 및 저장을 위한 API(ws)
    @MessageMapping("/love")
    @SendTo("/sub/love")
    public ChatRes.LoveChatRes sendToLoveChattingRoom(ChatReq.LoveChatReq loveChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        loveChatService.saveChat(loveChatReq, messageSendingTime);

        return new ChatRes.LoveChatRes().builder()
                .memberNickname(loveChatReq.getMemberNickname())
                .messageContent(loveChatReq.getMessageContent())
                .messageSendingTime(messageSendingTime)
                .build();
    }

    // 기숙사 채팅 내역을 불러오기 위한 API(http)
    @Tag(name = "chatting")
    @ApiOperation(value = "연애 상담 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{page}")
    public ResponseEntity<BaseResponse<ChatRes.LoveChatListRes>>loadLoveChattingList(@PathVariable int page) {

        ChatRes.LoveChatListRes chattingList = loveChatService.getChattingList(page, 10);

        if(chattingList == null)
            return ResponseEntity.ok(BaseResponse.ok(CHAT_OVERFLOW_THE_RANGE));


        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList));
    }
}
