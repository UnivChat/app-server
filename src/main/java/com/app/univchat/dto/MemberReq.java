package com.app.univchat.dto;

import lombok.*;

public class MemberReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class EmailAuthReq {
        private String email;
    }

}
