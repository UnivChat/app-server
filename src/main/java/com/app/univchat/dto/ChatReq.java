package com.app.univchat.dto;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.chat.OTOChatVisible;
import com.app.univchat.domain.*;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Optional;

@Setter
@Getter
public class ChatReq {

    @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
    @NotNull
    protected String memberNickname; // 송신자 식별자

    @ApiModelProperty(name = "채팅 내용", example = "채팅내용~~~")
    @NotNull
    protected String messageContent;


    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @ApiModel(value = "DormChatReq - 채팅 메시지 전송 객체")
    public static class DormChatReq extends ChatReq {

        public DormChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return DormChat.builder()
                    .member(member.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_ERROR)))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }


    /*
        연애 상담 채팅 req
     */
    @NoArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "LoveChatReq - 채팅 메시지 전송 객체")
    public static class LoveChatReq extends ChatReq {

        public LoveChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return LoveChat.builder()
                    .member(member.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_ERROR)))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "OTOChatReq - 1:1 채팅 메시지 전송 객체")
    public static class OTOChatReq extends ChatReq {

//        @ApiModelProperty(name = "채팅방 번호")
//        @NotNull
//        protected Long roomId; // 채팅방 id

        @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
        @NotNull
        protected String memberNickname; // 송신자 식별자

        @ApiModelProperty(name = "채팅 내용", example = "채팅내용~~~")
        @NotNull
        protected String messageContent;

        public OTOChat toEntity(Optional<OTOChatRoom> room,Optional<Member> member, String messageSendingTime) throws Exception {
            return OTOChat.builder()
                    .room(room.orElseThrow(() -> new BaseException(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR)))
                    .member(member.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_ERROR)))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "OTOChatRoomReq - 1:1 채팅방 개설 객체")
    public static class OTOChatRoomReq extends ChatReq {

        @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
        @NotNull
        protected String senderNickname; // 송신자 식별자

        @ApiModelProperty(name = "수신자 닉네임", example = "닉네임2")
        @NotNull
        protected String receiveNickname; // 수신자 식별자

//        protected OTOChatVisible='ALL';

        public OTOChatRoom toEntity(Optional<Member> sender,Optional<Member> receive) throws Exception {
            return OTOChatRoom.builder()
                    .sender(sender.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .receive(receive.orElseThrow(() -> new Exception("존재하지 않는 회원니다.")))
                    .visible(OTOChatVisible.ALL)
//                    .lastMessageId(null)
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "LiveChatReq - 채팅 메시지 전송 객체")
    public static class LiveChatReq extends ChatReq {

        public LiveChat toEntity(Member member, String messageSendingTime) {
            return LiveChat.builder()
                    .member(member)
                    .messageContent(this.messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }
}
