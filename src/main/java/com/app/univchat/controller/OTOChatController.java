package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.service.LoveChatService;
import com.app.univchat.service.OTOChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Tag(name = "one_to_one_chat", description = "1:1 채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/oto")
public class OTOChatController {

    private final OTOChatService otoChatService;

    /*
        1:1 채팅방 개설 api
     */
    @Tag(name = "one_to_one_chat")
    @ApiOperation(value = "1:1 채팅방 개설 API")
    @PostMapping("/room")
    public BaseResponse<ChatRes.OTOChatRoomRes> createChatRoom(@RequestBody ChatReq.OTOChatRoomReq otoChatRoomReq){

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,otoChatService.createChatRoom(otoChatRoomReq));

    }

//     1:1 채팅 송신 및 저장을 위한 API(ws)
    @MessageMapping("/oto")
    @SendTo("/sub/oto")
    public ChatRes.OTOChatRes sendToOTOChattingRoom(ChatReq.OTOChatReq otoChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        otoChatService.saveChat(otoChatReq, messageSendingTime);

        return new ChatRes.OTOChatRes().builder()
                .memberNickname(otoChatReq.getMemberNickname())
                .messageContent(otoChatReq.getMessageContent())
                .messageSendingTime(messageSendingTime)
                .build();
    }

//     1:1 채팅 내역을 불러오기 위한 API(http)
    @Tag(name = "chatting")
    @ApiOperation(value = "1:1 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/chat/{roomId}/{page}")
    public ResponseEntity<BaseResponse<List<ChatRes.OTOChatRes>>>loadOTOChattingList(@PathVariable Long roomId, @PathVariable int page) {

        List<ChatRes.OTOChatRes> chattingList = otoChatService.getChattingList(roomId,page);

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList));
    }
}