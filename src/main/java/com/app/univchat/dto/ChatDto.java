package com.app.univchat.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
// 사용자 정보 및 채팅 정보를 담을 Dto. 추후 코드 스타일 변경 및 필드 추가 필요
public class ChatDto {
    private String content;
}
