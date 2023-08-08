package com.app.univchat.dto;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.ClassChat;
import com.app.univchat.domain.ClassRoom;
import com.app.univchat.domain.Member;
import lombok.*;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClassChatReq {

    @ApiModelProperty(name = "송신자 닉네임", example = "닉네임1")
    @NotNull
    protected String memberNickname; // 송신자 식별자

    @ApiModelProperty(name = "채팅 내용", example = "채팅내용~~~")
    @NotNull
    protected String messageContent;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @ApiModel(value = "ClassChatReq - 클래스 채팅 메시지 전송 객체")
    public static class Chat extends ClassChatReq {

        public ClassChat toEntity(Optional<ClassRoom> classRoom,Optional<Member> member, String messageSendingTime) throws Exception {
            return ClassChat.builder()
                    .classRoom(classRoom.orElseThrow(() -> new BaseException(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR)))
                    .member(member.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_ERROR)))
                    .messageContent(messageContent)
                    .messageSendingTime(messageSendingTime)
                    .build();
        }
    }
}
