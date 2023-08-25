package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.*;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.FCMNotificationService;
import com.app.univchat.service.MemberService;
import com.app.univchat.service.chat.OTOChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.app.univchat.base.BaseResponseStatus.SUCCESS;

@Tag(name = "chatting-OTO", description = "일대일 채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/oto")
public class OTOChatController {

    private final OTOChatService otoChatService;
    private final MemberService memberService;
    private final FCMNotificationService fcmNotificationService;

    /*
        1:1 채팅방 개설 api
     */
    @Tag(name = "chatting-OTO")
    @ApiOperation(value = "1:1 채팅방 개설 API")
    @PostMapping("/room")
    public BaseResponse<ChatRes.OTOChatRoomRes> createChatRoom(@RequestBody ChatReq.OTOChatRoomReq otoChatRoomReq){

        // 탈퇴한 회원과 채팅방 개설 불가 - 수신자가 탈퇴회원인지 확인
        if(memberService.checkDeletedMember(otoChatRoomReq.getReceiverNickname())) {
            return BaseResponse.ok(BaseResponseStatus.USER_ALREADY_DELETED_ERROR);
        }

        return BaseResponse.ok(SUCCESS, otoChatService.createChatRoom(otoChatRoomReq));
    }

//     1:1 채팅 송신 및 저장을 위한 API(ws)

    @Tag(name = "chatting-OTO")
    @MessageMapping("/oto/{roomId}")
    @SendTo("/sub/oto/{roomId}")
    public ChatRes.OTOChatRes sendToOTOChattingRoom(@DestinationVariable Long roomId, ChatReq.OTOChatReq otoChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        String plainMessageContent = otoChatReq.getMessageContent();
        otoChatService.saveChat(roomId, otoChatReq, messageSendingTime);    // 1:1 채팅 메시지 저장
        otoChatReq.setMessageContent(plainMessageContent);  // 암호화 전 Original message set해서 알림 전송
        fcmNotificationService.sendOTOChatNotificationByToken(roomId, otoChatReq); // 1:1 채팅 알림 전송

        return new ChatRes.OTOChatRes().builder()
                .memberNickname(otoChatReq.getMemberNickname())
                .messageContent(plainMessageContent)
                .messageSendingTime(messageSendingTime)
                .build();
    }

//     1:1 채팅 내역을 불러오기 위한 API(http)
    @Tag(name = "chatting-OTO")
    @ApiOperation(value = "1:1 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{roomId}/{page}")
    public BaseResponse<ChatRes.OTOChatListRes>loadOTOChattingList(@PathVariable(value = "roomId") Long roomId,
                                                                     @PathVariable(value = "page") int page) {

        ChatRes.OTOChatListRes chattingList = otoChatService.getChattingList(roomId, page);

        return BaseResponse.ok(SUCCESS, chattingList);
    }

    @Tag(name = "chatting-OTO")
    @ApiOperation(value = "채팅방 나가기 API")
    @PutMapping("/exit/{roomId}")
    public BaseResponse<String> exitChatRoom(@PathVariable Long roomId,
                                             @ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        boolean isVisible = otoChatService.checkVisible(roomId);

        String exitRes ="채팅방을 나갔습니다";

        // 모든 유저가 채팅방에 남아있을 경우
        if(isVisible) otoChatService.exitChatRoom(roomId, member.getMember());
        // 아닐 경우 채팅방 삭제
        else otoChatService.deleteChatRoom(roomId, member.getMember());

        return BaseResponse.ok(SUCCESS, exitRes);

    }
    @Tag(name = "chatting-OTO")
    @ApiOperation(value = "채팅방 삭제 API")
    @DeleteMapping("/{roomId}")
    public BaseResponse<String> deleteChatRoom(@PathVariable Long roomId,
                                               @ApiIgnore @AuthenticationPrincipal PrincipalDetails member) throws IOException {

        boolean isVisible= otoChatService.checkVisible(roomId);

        String deleteRes = "채팅방 삭제 권한이 없습니다.";

        if(!isVisible) deleteRes = otoChatService.deleteChatRoom(roomId, member.getMember());

        return BaseResponse.ok(SUCCESS, deleteRes);

    }



    //     사용자별 참여하고 있는 1:1 채팅방 목록 조회를 위한 API
    @Tag(name = "chatting-OTO")
    @ApiOperation(value = "사용자별 1:1 채팅방 목록 API", notes = "사용자가 참여하고 있는 1:1 채팅방 목록을 반환합니다.")
    @GetMapping("/rooms")
    public BaseResponse<List<ChatRes.OTOChatRoomRes>> loadOTOChattingRoomList(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) {

        List<ChatRes.OTOChatRoomRes> chattingRoomList = otoChatService.getChattingRoomList(member.getMember());

        if(chattingRoomList.isEmpty()) {
            return BaseResponse.ok(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR);
        }

        return BaseResponse.ok(SUCCESS, chattingRoomList);
    }
}
