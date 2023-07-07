package com.app.univchat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMRequestDto {
    private Long targetUserId;
    private String title;
    private String body;
// private String image:
// private Map<String, String> data;


    @Builder
    public FCMRequestDto(Long targetUserId, String title, String body) {
        this.targetUserId =targetUserId;
        this.title = title;
        this.body = body;
    }
}