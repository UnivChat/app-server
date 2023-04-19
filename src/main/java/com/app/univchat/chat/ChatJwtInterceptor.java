package com.app.univchat.chat;

import com.app.univchat.jwt.JwtProvider;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatJwtInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // message에 대한 header 접근자
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        
        // header의 Authorization 값 저장
        String authorization = String.valueOf(headerAccessor.getNativeHeader("Authorization"));

        // command가 SEND(메세지 전송)가 아닌 경우, Jwt 검사를 하지 않음.
        if(!(StompCommand.SEND.equals(headerAccessor.getCommand()))) {
            return message;
        }

        // authorization 값이 없는 경우 예외 발생
        if(authorization == null || "null".equals(authorization)) {
            throw new MalformedJwtException("No exist Jwt");
        }

        // Jwt 파싱
        String prefix = "Bearer ";
        String jwt = authorization.substring(prefix.length());

        // Jwt 토큰 유효성 체크
        if(!(jwtProvider.validateToken(jwt))) {
            throw new MalformedJwtException("No valid Jwt.");
        }

        return message;
    }
}
