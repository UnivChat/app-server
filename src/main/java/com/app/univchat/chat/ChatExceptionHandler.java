package com.app.univchat.chat;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class ChatExceptionHandler extends StompSubProtocolErrorHandler {
    public ChatExceptionHandler() {
        super();
    }

    // ERROR 발생 시 실행, 웹소켓 연결 해제
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        System.out.println("ex.getMessage(): " + ex.getMessage());
        System.out.println("ex.getCause() = " + ex.getCause());

        if (ex.getCause() instanceof BaseException) {
            return baseExceptionErrorMessage(((BaseException) ex.getCause()).getStatus());
        } else if(ex.getCause().getMessage().contains("Jwt")) {
            return chatJwtErrorMessage();
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // Jwt 예외 처리를 위한 메세지
    private Message<byte[]> chatJwtErrorMessage() {
        String message = String.valueOf(BaseResponseStatus.JWT_ACCESS_DENIED.getMessage());
        String code = String.valueOf(BaseResponseStatus.JWT_ACCESS_DENIED.getCode());
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);

        headerAccessor.setMessage(code);
        headerAccessor.setLeaveMutable(true); // headerAccessor 수정 가능하도록 설정

        // Message 형식에 맞도록 Error 메시지 반환
        return MessageBuilder.createMessage(message.getBytes(), headerAccessor.getMessageHeaders());
    }

    // baseException 처리를 위한 메세지
    private Message<byte[]> baseExceptionErrorMessage(BaseResponseStatus baseResponseStatus) {
        //메시지 바디 설정
        String message = String.valueOf(baseResponseStatus.getMessage());
        String code = String.valueOf(baseResponseStatus.getCode());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(code);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

}
