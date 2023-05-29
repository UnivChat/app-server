package com.app.univchat.dto;

import lombok.*;

import java.util.List;

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
    // 라이브 채팅 메시지 객체
    public static class LiveChatListRes {
        private int maxPage;
        private int nowPage;
        private List<LiveChatRes> liveChatRes;
    }

}
