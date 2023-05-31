package com.app.univchat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

public class ChatRes {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 기숙사 채팅 메시지 객체
    public static class DormChatRes {

        private String memberNickname; // 송신자 식별자
        private String messageContent;
        private String messageSendingTime;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 연애상담 채팅 메시지 객체
    public static class LoveChatRes {
        private String memberNickname; // 송신자 식별자
        private String messageContent;
        private String messageSendingTime;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 라이브 채팅 메시지 객체
    public static class LiveChatRes {
        private String memberNickname; // 송신자 식별자
        private String messageContent;
        private String messageSendingTime;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    // 1:1 채팅 메시지 객체
    public static class OTOChatRes {
        private String memberNickname; // 송신자 식별자
        private String messageContent;
        private String messageSendingTime;
    }

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
    }
}
