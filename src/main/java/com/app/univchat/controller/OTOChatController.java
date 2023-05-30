package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.*;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.LoveChatService;
import com.app.univchat.service.OTOChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Tag(name = "one_to_one_chat", description = "1:1 채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("chatting/oto")
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

    @MessageMapping("/{roomId}")
    @SendTo("/sub/oto/{roomId}")
    public ChatRes.OTOChatRes sendToOTOChattingRoom(@DestinationVariable Long roomId, ChatReq.OTOChatReq otoChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        otoChatService.saveChat(roomId, otoChatReq, messageSendingTime);

        return new ChatRes.OTOChatRes().builder()
                .memberNickname(otoChatReq.getMemberNickname())
                .messageContent(otoChatReq.getMessageContent())
                .messageSendingTime(messageSendingTime)
                .build();
    }

//     1:1 채팅 내역을 불러오기 위한 API(http)
    @Tag(name = "chatting")
    @ApiOperation(value = "1:1 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{roomId}/{page}")
    public ResponseEntity<BaseResponse<List<ChatRes.OTOChatRes>>>loadOTOChattingList(@PathVariable Long roomId, @PathVariable int page) {

        List<ChatRes.OTOChatRes> chattingList = otoChatService.getChattingList(roomId,page);

        return ResponseEntity.ok(BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList));
    }

    @ApiOperation(value = "채팅방 나가기 API")
    @PutMapping("/exit/{roomId}")
    public BaseResponse<String> exitChatRoom(@PathVariable Long roomId, @ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        boolean isVisible= otoChatService.checkVisible(roomId);

        String exitRes="채팅방 나가기 권한이 없습니다.";
        if(isVisible) exitRes=otoChatService.exitChatRoom(roomId, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,exitRes);

    }

    @ApiOperation(value = "채팅방 삭제 API")
    @DeleteMapping("/delete/{roomId}")
    public BaseResponse<String> deleteChatRoom(@PathVariable Long roomId, @ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        boolean isVisible= otoChatService.checkVisible(roomId);

        String deleteRes="채팅방 삭제 권한이 없습니다.";
        if(!isVisible) deleteRes=otoChatService.deleteChatRoom(roomId, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS,deleteRes);

    }


}
