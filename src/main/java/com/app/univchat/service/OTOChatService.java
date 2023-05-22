package com.app.univchat.service;

import com.app.univchat.domain.Member;
import com.app.univchat.domain.OTOChatRoom;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.repository.OTOChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTOChatService {

    // MapperConfig에서 설정한 Mapper를 연결해주기 위함.
    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final OTOChatRoomRepository otoChatRoomRepository;

    /**
     *  1:1 채팅방 개설
     */
    @SneakyThrows
    @Transactional
    public String createChatRoom(ChatReq.OTOChatRoomReq otoChatRoomReq) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = otoChatRoomReq.getSenderNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);

        // nickname으로 수신자 member 객체 획득
        String receiveNickname = otoChatRoomReq.getReceiveNickname();
        Optional<Member> receive = memberService.getMember(receiveNickname);

        // 송신자 - 수신자 쌍이 이미 존재하면 채팅방 개설 불가
//        ChatRes.OTOChatRoomRes otoChatRoomRes = new ChatRes.OTOChatRoomRes();
//        Long id1=sender.get().getId();
//        Long id2=receive.get().getId();

        // 이미 채팅방 존재하면 해당 채팅방 id return
//        if(otoChatRoomRepository.existsBySenderAndReceive(id1,id2)) {
//            Optional<OTOChatRoom> foundRoom=otoChatRoomRepository.findBySenderAndReceive(id1,id2);
//
//            otoChatRoomRes.setRoomId(foundRoom.get().getRoomId());
//        }
//        else if(otoChatRoomRepository.existsBySenderAndReceive(id2,id1)) {
//            Optional<OTOChatRoom> foundRoom=otoChatRoomRepository.findBySenderAndReceive(id2,id1);
//
//            otoChatRoomRes.setRoomId(foundRoom.get().getRoomId());
//        }
//        else {  // 채팅방 개설
            otoChatRoomRepository.save(otoChatRoomReq.toEntity(sender,receive));
//            Optional<OTOChatRoom> foundRoom=otoChatRoomRepository.findBySenderAndReceive(id1,id2);
//
////            if(foundRoom.isEmpty()) System.out.println("엠티//////////////////");
//            otoChatRoomRes.setRoomId(foundRoom.get().getRoomId());
//        otoChatRoomRes.setRoomId(id1);
////        }
        return "채팅방이 개설되었습니다.";

    }

    /**
     *  1:1 채팅 메시지 저장
     */
//    @SneakyThrows
//    @Transactional
//    public void saveChat(ChatReq.LoveChatReq loveChatReq, String messageSendingTime) {
//
//        // nickname으로 송신자 member 객체 획득
//        String senderNickname = loveChatReq.getMemberNickname();
//        Optional<Member> sender = memberService.getMember(senderNickname);
//
//        // 채팅 내역 저장
//        loveChatRepository.save(loveChatReq.toEntity(sender, messageSendingTime));
//    }

    /**
     *  연애상담 채팅 내역 조회
     */
//    public List<ChatRes.LoveChatRes> getChattingList(int page) {
//
//        // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
//        Pageable pageable = PageRequest.of(page, 10,
//                                    Sort.by("messageSendingTime").descending());
//
//        // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
//        return loveChatRepository
//                        .findAll(pageable)
//                        .stream()
//                        .map(chat -> modelMapper.map(chat, ChatRes.LoveChatRes.class))
//                        .collect(Collectors.toList());
//    }
}
