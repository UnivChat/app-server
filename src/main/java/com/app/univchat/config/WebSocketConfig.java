package com.app.univchat.config;

import com.app.univchat.chat.ChatExceptionHandler;
import com.app.univchat.chat.ChatJwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatJwtInterceptor chatInterceptor;
    private final ChatExceptionHandler chatExceptionHandler;

    // 메시지 송수신을 위한 url prefix 설정 및 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub"); // 송신(/pub으로 시작하는 message는 @MessageMapping 메소드로 라우팅)
        registry.enableSimpleBroker("/sub"); // 수신(/sub로 시작하는 url로 해당 메시지를 수신할 수 있음)
    }

    // 웹소켓 접속 앤드 포인트 설정, 웹소켓 허용 주소 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(chatExceptionHandler); // 연결 시 예외 처리
        registry.addEndpoint("/chat") // websocket 엔드포인트
                .setAllowedOrigins("http://localhost:8080/*")
                .withSockJS(); // websocket 방식으로 안 될 경우 sockJS를 사용하여 연결
    }

    // 웹소켓 연결 후, 채팅 중 Jwt 인증을 위한 intercept 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInterceptor);
    }
}