package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.service.chat.LiveChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.app.univchat.base.BaseResponseStatus.CHAT_OVERFLOW_THE_RANGE;

@Tag(name = "chatting", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/live")
public class LiveChatController {

    private final LiveChatService liveChatService;

    @MessageMapping("/live")
    @SendTo("/sub/live")
    public ChatRes.LiveChatRes sendToDormChattingRoom(ChatReq.LiveChatReq liveChatReq) {
        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        String plainMessageContent = liveChatReq.getMessageContent();
        liveChatService.saveChat(liveChatReq, messageSendingTime); //채팅 내역 저장

        return new ChatRes.LiveChatRes().builder()
                .memberNickname(liveChatReq.getMemberNickname())
                .messageContent(plainMessageContent)
                .messageSendingTime(messageSendingTime)
                .build();
    }


    //page가 -1이면 최근 채팅 10개 보내기
    //반환 객체 안에 현재(최근)페이지 수가 몇인지 같이 반환
    @Tag(name = "chatting")
    @ApiOperation(value = "라이브 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{page}")
    public BaseResponse<ChatRes.LiveChatListRes> loadLiveChattingList(@PathVariable int page) {
        ChatRes.LiveChatListRes chattingList = liveChatService.getChattingList(page, 10);

        if(chattingList == null)
            return BaseResponse.ok(CHAT_OVERFLOW_THE_RANGE);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList);
    }


}
