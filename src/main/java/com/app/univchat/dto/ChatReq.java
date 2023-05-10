package com.app.univchat.dto;

import com.app.univchat.domain.DormChat;
import com.app.univchat.domain.Member;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Optional;

public class ChatReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ApiModel(value = "DormChatReq - 채팅 메시지 전송 객체")
    public static class DormChatReq extends ChatReq {

        @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
        @NotNull
        protected String memberNickname; // 송신자 식별자

        @ApiModelProperty(name = "채팅 내용", example = "채팅내용~~~")
        @NotNull
        protected String messageContent;

        public DormChat toEntity(Optional<Member> member, String messageSendingTime) throws Exception {
            return DormChat.builder()
                    .member(member.orElseThrow(() -> new Exception("존재하지 않는 회원입니다.")))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }
}
