package com.app.univchat.service;

import com.app.univchat.domain.Fcm;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.FcmReq;
import com.app.univchat.repository.FcmRepository;
import com.app.univchat.repository.MemberRepository;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
//@Slf4j
@Service
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final FcmRepository fcmRepository;

    public String sendNotificationByToken(FcmReq.Notification fcmNotificationReq) {
        Optional<Member> member=memberRepository.findById(fcmNotificationReq.getId());
        Optional<Fcm> target=fcmRepository.findByMember(member.get());    // target member 객체

        if(target.isPresent()) {
            if(target.get().getToken()!=null) {
                Notification notification=Notification.builder()
                        .setTitle(fcmNotificationReq.getTitle())
                        .setBody(fcmNotificationReq.getBody())
                        .build();

                Message message=Message.builder()
                        .setToken(target.get().getToken())
                        .setNotification(notification)
                        .build();

                try {
                    firebaseMessaging.send(message);
                    return "알람을 성공적으로 전송했습니다.";
                } catch (FirebaseMessagingException e){
                    e.printStackTrace();
                    return "알람 전송을 실패했습니다.";
                }

            }
            else {
                return "해당 유저의 FirebaseToken이 존재하지 않습니다.";
            }
        }
        else {
            return "해당 유저가 존재하지 않습니다.";
        }
    }
}
