package com.app.univchat.dto;

import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClassChatRes {

    protected String memberEmail;
    protected String memberNickname;
    protected String messageContent;
    protected String messageSendingTime;

    @Setter
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    // 클래스 채팅 메시지 객체
    public static class Chat extends ClassChatRes {}

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 클래스 채팅 메시지 목록
    public static class ChatList {
        private int maxPage;
        private int nowPage;
        private List<Chat> classChatList;
    }

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 클래스 채팅방
    public static class ChattingRoom {
        private ClassRoomDto classRoom;
        private String lastMessageSendingTime;
        private int numberOfUnreadMessage;
    }

}
