package com.app.univchat.service;

import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChatRoom;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.dto.FcmReq;
import com.app.univchat.repository.MemberRepository;
import com.app.univchat.repository.OTOChatRoomRepository;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
//@Slf4j
@Service
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final OTOChatRoomRepository otoChatRoomRepository;

    public String sendNotificationByToken(Long roomId, ChatReq.OTOChatReq otoChatReq) {
        // roomId로 채팅방 객체 획득
        Optional<OTOChatRoom> room = otoChatRoomRepository.findByRoomId(roomId);

        Member target;  // target member 객체: 채팅 메시지 수신자

        // 송신자가 receiver일 경우 수신자는 sender
        if(room.get().getReceiver().equals(otoChatReq.getMemberNickname())) {
            target=room.get().getSender();
        }
        else {
            target=room.get().getReceiver();
        }

        if(target!=null) {
            if(target.getFirebaseToken()!=null) {
                Notification notification=Notification.builder()
                        .setTitle(otoChatReq.getMemberNickname())   // 송신자 닉네임
                        .setBody(otoChatReq.getMessageContent())    // 채팅 내용
                        .build();

                Message message=Message.builder()
                        .setToken(target.getFirebaseToken())
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
