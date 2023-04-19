package com.app.univchat.dto;

import lombok.*;

public class MemberRes {


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class EmailAuthRes {
        int checkCode;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class InfoRes {
        String email;
        String nickname;
        String gender;
        String profileImgUrl;
    }




}
