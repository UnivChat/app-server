package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.chat.ClassChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.app.univchat.base.BaseResponseStatus.SUCCESS;

@Tag(name = "chatting", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/class")
public class ClassChatController {

    private final ClassChatService classChatService;

    // 클래스 채팅 송신 및 저장을 위한 API
    @MessageMapping("/class/{roomId}")
    @SendTo("/sub/class/{roomId}")
    public ChatRes.ClassChatRes sendToOTOChattingRoom(@DestinationVariable String roomId, ChatReq.ClassChatReq classChatReq) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        String plainMessageContent = classChatReq.getMessageContent();
        classChatService.saveChat(roomId, classChatReq, messageSendingTime);

        return new ChatRes.ClassChatRes().builder()
                .memberNickname(classChatReq.getMemberNickname())
                .messageContent(plainMessageContent)
                .messageSendingTime(messageSendingTime)
                .build();
    }


    // 클래스 채팅 내역을 불러오기 위한 API
    @Tag(name = "chatting")
    @ApiOperation(value = "클래스 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{roomId}/{page}")
    public BaseResponse<ChatRes.ClassChatListRes>loadClassChattingList(@PathVariable(value = "roomId") String roomId,
                                                                   @PathVariable(value = "page") int page) {

        ChatRes.ClassChatListRes chattingList = classChatService.getChattingList(roomId, page);

        return BaseResponse.ok(SUCCESS, chattingList);
    }

    // 사용자별 참여하고 있는 클래스 채팅방 목록 조회를 위한 API
    @Tag(name = "chatting")
    @ApiOperation(value = "사용자별 클래스 채팅방 목록 API", notes = "사용자가 참여하고 있는 클래스 채팅방 목록을 반환합니다.")
    @GetMapping("/rooms")
    public BaseResponse<List<ChatRes.ClassChatRoomRes>> loadOTOChattingRoomList(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) {

        List<ChatRes.ClassChatRoomRes> chattingRoomList = classChatService.getChattingRoomList(member.getMember());

        if(chattingRoomList.isEmpty()) {
            return BaseResponse.ok(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR);
        }

        return BaseResponse.ok(SUCCESS, chattingRoomList);
    }

    // 클래스 전체 목록 조회를 위한 API
    @Tag(name = "chatting")
    @ApiOperation(value = "사용자별 클래스 채팅방 목록 API", notes = "클래스 목록을 10개씩 반환합니다. 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{page}")
    public BaseResponse<ChatRes.ClassChatRoomListRes> loadClassChattingRoomListAll(@PathVariable(value = "page") int page) {

        ChatRes.ClassChatRoomListRes classChattingRoomList = classChatService.getChattingRoomListAll(page);

        if(classChattingRoomList.getClassChatRoomList().isEmpty()) {
            return BaseResponse.ok(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR);
        }

        return BaseResponse.ok(SUCCESS, classChattingRoomList);
    }

    @Tag(name = "chatting")
    @ApiOperation(value = "채팅방 들어가기 API", notes = "자신의 클래스 목록에서 해당 클래스를 추가합니다.")
    @DeleteMapping("/enter/{roomId}")
    public BaseResponse<String> enterClassChattingRoom(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member
                                                     , @PathVariable(value = "roomId") String roomId) {

        String result = classChatService.insertChattingRoomMemberByClassNumber(roomId, member.getMember());

        return BaseResponse.ok(SUCCESS, result);
    }

    @Tag(name = "chatting")
    @ApiOperation(value = "채팅방 나가기 API", notes = "자신의 클래스 목록에서 해당 클래스를 제거합니다.")
    @DeleteMapping("/exit/{roomId}")
    public BaseResponse<String> exitClassChattingRoom(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member
                                                    , @PathVariable(value = "roomId") String roomId) {

        String result = classChatService.deleteChattingRoomMemberByClassNumber(roomId, member.getMember());

        return BaseResponse.ok(SUCCESS, result);
    }

    // TODO: 최근 메세지, 시간, 사용자 조회, 참여 인원
}
