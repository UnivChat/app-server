package com.app.univchat.controller;

import com.app.univchat.dto.ChatDto;
import com.app.univchat.dto.MessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/room") // 방종류
public class ChatController {

    // messageMapping 메시지가 /hello로 전송이 되는 경우 메소드가 호출됨.
    @MessageMapping("/{roomId}") //  /방번호 -> pub은 prefix 설정에 의해 생략
    @SendTo("/sub/class/{roomId}") // /sub(구독 식별자)/방종류/방번호 => 해당 방 구독자에게 메시지 전송
    // @Pathvariable이 아닌 @DestinationVariable을 사용해야한다.
    public ChatDto chatToGreetings(@DestinationVariable Long roomId, MessageDto message) {

        // 웹소켓 응답 형식의 body에 json이 string 형태로 들어간다. (받을 때 응답 body를 json.parse 해야한다.)
        return new ChatDto("Hello, " + message.getName() + ". This room is " + roomId);
    }
}
