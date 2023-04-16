package com.app.univchat.dto;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    public static class Update {
        @NotNull
        @ApiModelProperty(name = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(name = "프로필 이미지 url", example = "https://~~~~~~~~~.jpg")
        private String profileImageUrl;
    }


}
