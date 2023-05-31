package com.app.univchat.chat;

import com.amazonaws.util.json.Jackson;
import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.repository.OTOChatRoomRepository;
import com.app.univchat.service.MemberService;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
public class ChatSendInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final OTOChatRoomRepository otoChatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // message에 대한 header 접근자
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        
        // header의 Authorization 값 저장
        String authorization = String.valueOf(headerAccessor.getNativeHeader("Authorization"));

        // command가 SUBSCRIBE인 경우 구독경로 검사
        if(StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            System.out.println("headerAccessor = " + headerAccessor.getDestination());
            checkSub(headerAccessor.getDestination());
            return message;
        }

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
        String email = jwtProvider.getEmail(jwt);

        // 닉네임 DB에 있는지 체크 & jwt와 일치하는지 확인
        checkNickname(message, email);

        return message;
    }

    /**
     * 닉네임 DB에 있는지 체크 & jwt와 일치하는지 확인
     */
    @SneakyThrows
    public void checkNickname(Message<?> message, String email) {

        // message payload에서 닉네임 얻기
        String payload = new String((byte[])message.getPayload());
        ChatReq.LiveChatReq body = Jackson.fromJsonString(payload, ChatReq.LiveChatReq.class);
        System.out.println("sender nickname: " + body.getMemberNickname());

        // DB에 닉네임 있는지 확인
        Member member = memberService.getMember(body.getMemberNickname()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_NICKNAME_ERROR)
        );

        // jwt 토큰과 닉네임으로 찾은 member 객체 일치하는지 확인
        if (!member.getNickname().equals(email)) {
            throw new BaseException(BaseResponseStatus.JWT_AND_NICKNAME_DONT_MATCH);
        }
    }

    /**
     * 구독 URI 체크
     */
    public void checkSub(String sub) {
        String[] splitSub = sub.split("/");
        Long roomId = null;

        //일대일 채팅 구독일 경우 만들어진 채팅방인지 확인
        if(splitSub[2].equals("oto")) {
            // roomId 추출
            try {
                roomId = Long.valueOf(Integer.parseInt(splitSub[3]));

            } catch (ArrayIndexOutOfBoundsException e) {
                throw new BaseException(BaseResponseStatus.UNSUPPORTED_SUBSCRIBE_URI);
            }

            // roomId DB에 있는지 체크
            otoChatRoomRepository.findByRoomId(roomId).orElseThrow(
                    () -> new BaseException(BaseResponseStatus.UNSUPPORTED_SUBSCRIBE_URI)
            );
        }

    }
}
