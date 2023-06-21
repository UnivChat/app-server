package com.app.univchat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRes {

    protected String memberNickname;
    protected String messageContent;
    protected String messageSendingTime;

    @SuperBuilder
    @AllArgsConstructor
    @Setter
    @Getter
    // 기숙사 채팅 메시지 객체
    public static class DormChatRes extends ChatRes { }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 기숙사 채팅 메시지 객체
    public static class DormChatListRes {
        private int maxPage;
        private int nowPage;
        private List<DormChatRes> dormChatRes;
    }

    @SuperBuilder
    @AllArgsConstructor
    @Setter
    @Getter
    // 연애상담 채팅 메시지 객체
    public static class LoveChatRes extends ChatRes { }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 연애상담 채팅 메시지 객체
    public static class LoveChatListRes {
        private int maxPage;
        private int nowPage;
        private List<LoveChatRes> loveChatRes;
    }



    @SuperBuilder
    @AllArgsConstructor
    @Setter
    @Getter
    // 라이브 채팅 메시지 객체
    public static class LiveChatRes extends ChatRes { }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 라이브 채팅 메시지 객체
    public static class LiveChatListRes {
        private int maxPage;
        private int nowPage;
        private List<LiveChatRes> liveChatRes;
    }



    @SuperBuilder
    @AllArgsConstructor
    @Setter
    @Getter
    // 1:1 채팅 메시지 객체
    public static class OTOChatRes extends ChatRes { }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ApiModel(value = "1:1 채팅방 목록 조회 api 응답 객체")
    // 1:1 채팅방 객체
    public static class OTOChatRoomRes {
        @ApiModelProperty(name = "채팅방 식별자", example = "1234")
        private Long roomId; // 채팅방 식별자

        @ApiModelProperty(name = "상대방 닉네임", example = "닉네임")
        private String opponentNickname; // 상대방 닉네임

        @ApiModelProperty(name = "가장 최근 메세지 내용", example = "채팅 내용")
        private String lastMessageSendingTime; // 마지막 메세지 송신 시각

        @ApiModelProperty(name = "가장 최근 메세지 송신 시각", example = "2023-05-29 01:23:45:678")
        private String lastMessageContent; // 마지막 메세지 내용

        public OTOChatRoomRes(Long roomId, String opponentNickname) {
            this.roomId = roomId;
            this.opponentNickname = opponentNickname;
            lastMessageContent = " ";
            lastMessageSendingTime = " ";
        }
    }
}
