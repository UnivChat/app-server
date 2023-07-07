package com.app.univchat.service;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.domain.Fcm;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.FcmReq;
import com.app.univchat.dto.FcmReq;
import com.app.univchat.repository.FcmRepository;
import com.app.univchat.repository.MemberRepository;
import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.google.api.services.storage.model.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
//@Slf4j
@Service
public class FCMNotificationService {
//    @Value("${fcm.key.path}")
//    private String FCM_PRIVATE_KEY_PATH;
//
//    // messaging 만 권한 설정
//    @Value("${fcm.key.scope}")
//    private String firebaseScope;
//
//    // fcm 기본 설정
//    @PostConstruct
//    public void init() {
//        try {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(
//                            GoogleCredentials
//                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
//                                    .createScoped(List.of(firebaseScope)))
//                    .build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                log.info("Firebase application has been initialized");
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            // spring 뜰때 알림 서버가 잘 동작하지 않는 것이므로 바로 죽임
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    // 알림 보내기
//    public void sendByTokenList(List<String> tokenList,String title, String body) {
//
//        // 메시지 만들기
//        List<Message> messages = tokenList.stream().map(token -> Message.builder()
//                .putData("time", LocalDateTime.now().toString())
//                .setNotification(new Notification(title, body))
//                .setToken(token)
//                .build()).collect(Collectors.toList());
//
//        // 요청에 대한 응답을 받을 response
//        BatchResponse response;
//        try {
//
//            // 알림 발송
//            response = FirebaseMessaging.getInstance().sendAll(messages);
//
//            // 요청에 대한 응답 처리
//            if (response.getFailureCount() > 0) {
//                List<SendResponse> responses = response.getResponses();
//                List<String> failedTokens = new ArrayList<>();
//
//                for (int i = 0; i < responses.size(); i++) {
//                    if (!responses.get(i).isSuccessful()) {
//                        failedTokens.add(tokenList.get(i));
//                    }
//                }
//                log.error("List of tokens are not valid FCM token : " + failedTokens);
//            }
//        } catch (FirebaseMessagingException e) {
//            log.error("cannot send to memberList push message. error info : {}", e.getMessage());
//        }
//    }

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final FcmRepository fcmRepository;

    public String sendNotificationByToken(FcmReq.Notification fcmNotificationReq) {
        Optional<Member> member=memberRepository.findById(fcmNotificationReq.getId());
//        if(member.isEmpty()) System.out.println("-----널널널--------");
//        else System.out.println("-----통과--------");
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
