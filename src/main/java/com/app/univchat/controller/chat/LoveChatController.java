package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.service.chat.LoveChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.app.univchat.base.BaseResponseStatus.CHAT_OVERFLOW_THE_RANGE;

@Tag(name = "chatting-love", description = "연애 상담 채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/love")
public class LoveChatController {

    private final LoveChatService loveChatService;

    // 연애상담 채팅 송신 및 저장을 위한 API(ws)
    @Tag(name = "chatting-love")
    @MessageMapping("/love")
    @SendTo("/sub/love")
    public ChatRes.LoveChatRes sendToLoveChattingRoom(ChatReq.LoveChatReq loveChatReq,
                                                      SimpMessageHeaderAccessor accessor) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        String plainMessageContent = loveChatReq.getMessageContent();
        String authorization = String.valueOf(accessor.getNativeHeader("Authorization"));
        loveChatService.saveChat(loveChatReq, messageSendingTime, authorization);

        return new ChatRes.LoveChatRes().builder()
                .memberNickname(loveChatReq.getMemberNickname())
                .messageContent(plainMessageContent)
                .messageSendingTime(messageSendingTime)
                .build();
    }

    // 기숙사 채팅 내역을 불러오기 위한 API(http)
    @Tag(name = "chatting-love")
    @ApiOperation(value = "연애 상담 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환합니다. \n\n 처음 채팅방 입장 시 page = -1로 요청해주세요. 이후 이전 채팅 목록 조회 시 응답으로 받은 nowPage에서 -1씩 줄이며 요청 보내면 됩니다.")
    @GetMapping("/{page}")
    public BaseResponse<ChatRes.LoveChatListRes>loadLoveChattingList(@PathVariable int page) {

        ChatRes.LoveChatListRes chattingList = loveChatService.getChattingList(page, 10);


        return BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList);
    }
}
