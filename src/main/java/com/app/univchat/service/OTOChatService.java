package com.app.univchat.service;

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
    }

    /**
     *  1:1 채팅 내역 조회
     */
    public List<ChatRes.OTOChatRes> getChattingList(Long roomId,int page) {

        // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
        Pageable pageable = PageRequest.of(page, 10,
                                    Sort.by("messageSendingTime").descending());


        // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
        return otoChatRepository
                        .findByRoom(roomId,pageable)
                        .stream()
                        .map(chat -> modelMapper.map(chat, ChatRes.OTOChatRes.class))
                        .collect(Collectors.toList());
    }
}
