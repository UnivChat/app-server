package com.app.univchat.dto;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Fcm;
import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChat;
import com.app.univchat.domain.OTOChatRoom;
import com.app.univchat.repository.MemberRepository;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.Optional;

//@NoArgsConstructor
//@Builder

public class FcmReq {

//    @Builder
    @NoArgsConstructor
//    @AllArgsConstructor
//    @Setter
    @Getter
    public static class Notification {
        @NotNull
        private Long id;

        private String title;

        private String body;

//        public Long getTarget() {
//            return target;
//        }
//
//        public String getTitle() {
//            return title;
//        }
//
//        public String getBody() {
//            return body;
//        }


        @Builder
        public Notification(Long id,String title, String body) {
            this.id=id;
            this.title=title;
            this.body=body;
        }

        // Dto를 entity 객체로 만들어 return해주는 메서드
//        public Fcm toEntity(Optional<Member> member, String token) throws Exception {
//            return Fcm.builder()
//                    .member(member.orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_ERROR)))
//                    .token(token)
//                    .build();
//        }
    }

//    @Builder
//    public FCMNotificationReq(Long targetId,String title, String body) {
//        this.targetId=targetId;
//        this.title=title;
//        this.body=body;
//    }
}
