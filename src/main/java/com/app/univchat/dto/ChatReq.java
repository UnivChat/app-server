package com.app.univchat.dto;

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
    @Getter
    @Setter
    @ApiModel(value = "DormChatReq - 채팅 메시지 전송 객체")
    public static class DormChatReq extends ChatReq {

        public DormChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return DormChat.builder()
                    .member(member.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }


    /*
        연애 상담 채팅 req
     */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "LoveChatReq - 채팅 메시지 전송 객체")
    public static class LoveChatReq extends ChatReq {

        @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
        @NotNull
        protected String memberNickname; // 송신자 식별자

        @ApiModelProperty(name = "채팅 내용", example = "채팅내용~~~")
        @NotNull
        protected String messageContent;

        public LoveChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return LoveChat.builder()
                    .member(member.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }

    /*
        1:1 채팅방 개설 req
     */
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

//        @ApiModelProperty(name = "볼 수 있는 상태 설정", notes = "0(모두 볼 수 있음), {나가지 않은 사람 id 개수}(나가지 않은 사람만 볼 수 있음), -1(모두 나간 경우 - 모두 볼 수 없음)")
//        protected int visible; // 볼 수 있는 상태 설정

        public OTOChatRoom toEntity(Optional<Member> sender,Optional<Member> receive) throws Exception {
            return OTOChatRoom.builder()
                    .sender(sender.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .receive(receive.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .visible(0)
                    .lastMessageId(null)
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
