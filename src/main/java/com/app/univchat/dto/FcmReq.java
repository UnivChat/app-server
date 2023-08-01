package com.app.univchat.dto;

import com.sun.istack.NotNull;
import lombok.*;

public class FcmReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Notification {
        @NotNull
        private Long id;

        private String title;

        private String body;
    }

}
