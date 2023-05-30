package com.app.univchat.service;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChat;
import com.app.univchat.domain.OTOChatRoom;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.repository.OTOChatRepository;
import com.app.univchat.repository.OTOChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OTOChatService {

    // MapperConfig에서 설정한 Mapper를 연결해주기 위함.
    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final OTOChatRoomRepository otoChatRoomRepository;
    private final OTOChatRepository otoChatRepository;

    /**
     *  1:1 채팅방 개설
     */
    @SneakyThrows
    @Transactional
    public ChatRes.OTOChatRoomRes createChatRoom(ChatReq.OTOChatRoomReq otoChatRoomReq) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = otoChatRoomReq.getSenderNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);

        // nickname으로 수신자 member 객체 획득
        String receiveNickname = otoChatRoomReq.getReceiveNickname();
        Optional<Member> receive = memberService.getMember(receiveNickname);

        // 송신자 - 수신자 쌍이 이미 존재하면 채팅방 개설 불가
        ChatRes.OTOChatRoomRes otoChatRoomRes = new ChatRes.OTOChatRoomRes();

        // 이미 채팅방 존재하면 해당 채팅방 id return
        Optional<OTOChatRoom> foundRoom;
        if(otoChatRoomRepository.findBySenderAndReceive(sender.get(),receive.get())!=null) {
            foundRoom=otoChatRoomRepository.findBySenderAndReceive(sender.get(),receive.get());
            if(foundRoom.isEmpty()) {
                foundRoom=otoChatRoomRepository.findBySenderAndReceive(receive.get(),sender.get());
            }
        }
        else {  // 채팅방 개설
            otoChatRoomRepository.save(otoChatRoomReq.toEntity(sender,receive));
            foundRoom=otoChatRoomRepository.findBySenderAndReceive(sender.get(),receive.get());
        }
        otoChatRoomRes.setRoomId(foundRoom.get().getRoomId());
        return otoChatRoomRes;

    }



    /**
     *  1:1 채팅 메시지 저장
     */
    @SneakyThrows
    @Transactional
    public void saveChat(ChatReq.OTOChatReq otoChatReq, String messageSendingTime) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = otoChatReq.getMemberNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);

        // roomId로 채팅방 객체 획득
        Long roomId=otoChatReq.getRoomId();
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);

        // 채팅 내역 저장
        otoChatRepository.save(otoChatReq.toEntity(room, sender ,messageSendingTime));

        // TODO: 채팅방의 lastMessageId 값 저장? OR lastMessageId 필드 삭제?
    }

    /**
     *  1:1 채팅 내역 조회
     */
    public List<ChatRes.OTOChatRes> getChattingList(Long roomId, int page) {

        // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
        Pageable pageable = PageRequest.of(page, 10,
                                    Sort.by("messageSendingTime").descending());

        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);

        // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
        return otoChatRepository
                        .findByRoom(room,pageable)
                        .stream()
                        .map(chat -> modelMapper.map(chat, ChatRes.OTOChatRes.class))
                        .collect(Collectors.toList());
    }

    /**
     * 1:1 채팅방 ID를 통해 가장 최근 메세지 조회
     */
    public OTOChat getLastMessage(OTOChatRoom room) {

        OTOChat lastMessage = otoChatRepository.findTop1ByRoomOrderByMessageSendingTimeDesc(room);

        return lastMessage;
    }

    /**
     *  사용자별 1:1 채팅방 목록 조회
     */
    public List<ChatRes.OTOChatRoomRes> getChattingRoomList(Member member) {

        // 사용자 ID를 통해 송신자 혹은 수신자의 ID와 같다면 해당 1:1 채팅방 조회
        // 참여하고 있는 1:1 채팅방이 없을 시, 응답 코드 404와 함께 에러 반환
        List<OTOChatRoom> result = otoChatRoomRepository.findBySenderOrReceive(member, member);
        
        // 조회한 1:1 채팅방 목록을 이용하여 응답 객체와 매핑
        List<ChatRes.OTOChatRoomRes> chattingRoomList = result.stream()
                // 채팅방별 매핑
                .map(chattingRoom -> {
                    // 상대방 닉네임 추출
                    Member sender = chattingRoom.getSender();
                    Member receiver = chattingRoom.getReceive();
                    String opponentNickname = member.getId() != sender.getId()? sender.getNickname(): receiver.getNickname();
                    System.out.println(chattingRoom);
                    System.out.println(opponentNickname);
                    System.out.println(chattingRoom.getRoomId());

                    // 마지막 메세지 추출
                    OTOChat lastMessage = getLastMessage(chattingRoom);
                    System.out.println(lastMessage);

                    // 빌더 패턴으로 응답 객체 반환
                    if(lastMessage != null) {
                        return(ChatRes.OTOChatRoomRes.builder()
                                .roomId(chattingRoom.getRoomId())
                                .opponentNickname(opponentNickname)
                                .lastMessageContent(lastMessage.getMessageContent())
                                .lastMessageSendingTime(lastMessage.getMessageSendingTime()).build());
                    } else {
                        return(ChatRes.OTOChatRoomRes.builder()
                                .roomId(chattingRoom.getRoomId())
                                .opponentNickname(opponentNickname)
                                .lastMessageContent("")
                                .lastMessageSendingTime("").build());
                    }

                })
                // 마지막 메세지를 기준으로 최신순 정렬
                .sorted(Comparator.comparing(ChatRes.OTOChatRoomRes::getLastMessageSendingTime).reversed())
                // 매핑한 결과를 리스트로 반환
                .collect(Collectors.toList());

        return chattingRoomList;
    }

}
