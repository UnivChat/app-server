package com.app.univchat.dto;

import com.app.univchat.domain.DormChat;
import com.app.univchat.domain.Member;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.Optional;

public class ChatReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class DormChatReq {
        @NotNull
        private Long roomId; // 기숙사 식별자

        @NotNull
        private String memberNickname; // 송신자 식별자

        @NotNull
        private String messageContent;

        public DormChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return DormChat.builder()
                    .roomId(roomId)
                    .member(member.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }
}
