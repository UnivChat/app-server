package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ClassChatReq;
import com.app.univchat.dto.ClassChatRes;
import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.FCMNotificationService;
import com.app.univchat.service.chat.ClassChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Tag(name = "chatting-class", description = "클래스 채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/class")
public class ClassChatController {

    private final ClassChatService classChatService;
    private final FCMNotificationService fcmNotificationService;

    /* *************** Class ************** */

    // 개설된 클래스 목록 조회 API
    @Tag(name = "chatting-class")
    @ApiOperation(value = "클래스 리스트 조회", notes = "50개씩 결과를 가져옵니다. \n\n 검색이 필요한 경우 query parameter로 className을 보내주시면 됩니다.")
    @GetMapping("{page}")
    public BaseResponse<List<ClassRoomDto>> getClassRoomList(@PathVariable int page,
                                                             @RequestParam(required = false) String className){
        List<ClassRoomDto> result = classChatService.getClassRoomList(page, className);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

    /* ************ Class Chat ************ */

    // 클래스 채팅 송수신 및 저장 API
    @MessageMapping("/class/{classNumber}")
    @SendTo("/sub/class/{classNumber}")
    public ClassChatRes.Chat sendToClassChattingRoom(@DestinationVariable String classNumber, ClassChatReq.Chat classChatReq,
                                                     SimpMessageHeaderAccessor accessor) {

        String messageSendingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        String plainMessageContent = classChatReq.getMessageContent();
        String authorization = String.valueOf(accessor.getNativeHeader("Authorization"));

        classChatService.saveChat(classNumber, classChatReq, messageSendingTime, authorization);   // class 채팅 내용 저장
        classChatReq.setMessageContent(plainMessageContent);  // 암호화 전 Original message set해서 알림 전송
        fcmNotificationService.sendClassChatNotificationByToken(classNumber, classChatReq); // class 채팅 알림 전송

        return ClassChatRes.Chat.builder()
                .memberNickname(classChatReq.getMemberNickname())
                .messageContent(plainMessageContent)
                .messageSendingTime(messageSendingTime)
                .build();
    }

    // 클래스 채팅 조회 API
    @Tag(name = "chatting-class")
    @ApiOperation(value = "클래스 채팅 내역 API", notes = "채팅 내역 최신순으로 10개를 반환하며, 페이지 번호는 0부터 시작합니다.")
    @GetMapping("/{classNumber}/{page}")
    public BaseResponse<ClassChatRes.ChatList>loadClassChattingList(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member,
                                                                    @PathVariable(value = "classNumber") String classNumber,
                                                                       @PathVariable(value = "page") int page) {

        ClassChatRes.ChatList chattingList = classChatService.getChattingList(classNumber, page, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingList);
    }

    /* ********** Class ChatRoom ********** */

    // 클래스 채팅방 조회 API
    @GetMapping("/rooms")
    @Tag(name = "chatting-class")
    @ApiOperation(value = "사용자별 클래스 채팅방 목록 API",
            notes = "사용자가 참여하고 있는 클래스 채팅방 목록을 반환합니다. 안 읽은 메세지 수, 가장 촤근 메세지 송신 시각을 포함합니다. 가장 최근 메세지 송신 시각은 채팅이 없을 경우 \"\"를 반환합니다.")
    public BaseResponse<List<ClassChatRes.ChattingRoom>> loadClassChattingRoomList(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member) {

        List<ClassChatRes.ChattingRoom> chattingRoomList = classChatService.getChattingRoomList(member.getMember());

        if(chattingRoomList.isEmpty()) {
            return BaseResponse.ok(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR);
        }

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, chattingRoomList);
    }

    // 클래스 채팅방 입장 (클래스 추가) API
    @Tag(name = "chatting-class")
    @ApiOperation(value = "채팅방 들어가기 API", notes = "자신의 클래스 목록에서 해당 클래스를 추가합니다.")
    @PostMapping("/enter/{classNumber}")
    public BaseResponse<String> enterClassChattingRoom(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member
            , @PathVariable(value = "classNumber") String classNumber) {

        String result = classChatService.insertChattingRoomMemberByClassNumber(classNumber, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

    // 클래스 채팅방 퇴장 (클래스 제거) API
    @Tag(name = "chatting-class")
    @ApiOperation(value = "채팅방 나가기 API", notes = "자신의 클래스 목록에서 해당 클래스를 제거합니다.")
    @DeleteMapping("/exit/{classNumber}")
    public BaseResponse<String> exitClassChattingRoom(@ApiIgnore @AuthenticationPrincipal PrincipalDetails member
            , @PathVariable(value = "classNumber") String classNumber) {

        String result = classChatService.deleteChattingRoomMemberByClassNumber(classNumber, member.getMember());

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }
}
