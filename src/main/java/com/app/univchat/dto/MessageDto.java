package com.app.univchat.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
// 사용자 정보와 메시지 내용을 받기 위한 Dto, 추후 필드 및 코드 스타일 변경 필요
public class MessageDto {
    private String name;
}
