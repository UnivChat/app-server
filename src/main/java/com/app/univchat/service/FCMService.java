package com.app.univchat.service;

import com.app.univchat.domain.Member;
import com.app.univchat.dto.FCMRequestDto;
import com.app.univchat.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;


    public String sendNotificationByToken(FCMRequestDto requestDto) {
        Optional<Member> member = memberRepository.findById(requestDto.getTargetUserId());

        if (member.isPresent()) {
            if (member.get().getFirebaseToken() != null) {
                Notification notification = Notification.builder()
                        .setTitle(requestDto.getTitle())
                        .setBody(requestDto.getBody())
                        // .setImage(requestDto.getImage())
                        .build();

                Message message = Message.builder()
                        .setToken(member.get().getFirebaseToken())
                        .setNotification(notification)
                        // .putAllData(requestDto.getData())
                        .build();


                try {
                    firebaseMessaging.send(message);
                    return "알림을 성공적으로 전송했습니다. targetUser Id=" + requestDto.getTargetUserId();
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return "알림 보내기를 실패했습니다  targetUser Id=" + requestDto.getTargetUserId();
                }
            } else {
                return "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId=" + requestDto.getTargetUserId();
            }

        } else {
            return "A HOA UC, targetUser Id=" + requestDto.getTargetUserId();
        }
    }
}