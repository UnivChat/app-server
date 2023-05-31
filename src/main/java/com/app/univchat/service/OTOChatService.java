package com.app.univchat.service;

import com.app.univchat.chat.OTOChatVisible;
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
        Optional<OTOChatRoom> foundRoom=null;
        if(otoChatRoomRepository.findBySenderAndReceive(sender.get(),receive.get())!=null) {
            foundRoom=otoChatRoomRepository.findBySenderAndReceive(sender.get(),receive.get());
            if(foundRoom.isEmpty()) {
                foundRoom=otoChatRoomRepository.findBySenderAndReceive(receive.get(),sender.get());
            }
        }
        if(foundRoom.isEmpty()) {  // 채팅방 개설
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
    public void saveChat(Long roomId, ChatReq.OTOChatReq otoChatReq, String messageSendingTime) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = otoChatReq.getMemberNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);

        // roomId로 채팅방 객체 획득
//        Long roomId=otoChatReq.getRoomId();
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

    public boolean checkVisible(Long roomId) {
        // 현재 참여 채팅방 객체
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);

        // 모든 유저가 채팅방에 남아있을 경우 true
        if(room.get().getVisible()== OTOChatVisible.ALL) return true;
        else return false;
    }

    /**
     *  1:1 채팅방 나가기
     */
    public String exitChatRoom(Long roomId, Member member) {
        // 채팅방 나가는 회원 id
        Long id=member.getId();

        // 현재 참여 채팅방 객체
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);
        OTOChatRoom chatRoom=room.get();

        // 채팅방 sender id
        Long sid=chatRoom.getSender().getId();   // sender id

        // sender 가 나갈 경우
        if(sid==id) {
            chatRoom.updateVisible(OTOChatVisible.RECEIVER);  // receiver만 볼 수 있음
        }
        // receiver 가 나갈 경우
        else {
            chatRoom.updateVisible(OTOChatVisible.SENDER);  // sender만 볼 수 있음
        }

        otoChatRoomRepository.save(chatRoom);

        return "채팅방을 나갔습니다.";
    }

    /**
     *  1:1 채팅방 삭제
     */
    public String deleteChatRoom(Long roomId, Member member) {
        // 채팅방 나가는 회원 id
        Long id=member.getId();

        // 현재 참여 채팅방 객체
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);
        OTOChatRoom chatRoom=room.get();

        // 채팅 내역 삭제하기
        otoChatRepository.deleteByRoom(room);
        otoChatRoomRepository.deleteByRoomId(roomId);

        return "채팅방이 삭제되었습니다.";
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
        List<OTOChatRoom> result = otoChatRoomRepository.findBySenderOrReceive(member, member);

        // visible에 따른 필터링
        if(result != null) {
            result = result.stream().filter(chattingRoom ->
                            chattingRoom.getVisible() == OTOChatVisible.ALL
                      ||   (chattingRoom.getVisible() == OTOChatVisible.SENDER && chattingRoom.getSender().getId() == member.getId())
                      ||   (chattingRoom.getVisible() == OTOChatVisible.RECEIVER && chattingRoom.getReceive().getId() == member.getId()))
                           .collect(Collectors.toList());
        }

        // 조회한 1:1 채팅방 목록을 이용하여 응답 객체와 매핑
        List<ChatRes.OTOChatRoomRes> chattingRoomList = result.stream()
                // 채팅방별 매핑
                .map(chattingRoom -> {
                    // 상대방 닉네임 추출
                    Member sender = chattingRoom.getSender();
                    Member receiver = chattingRoom.getReceive();
                    String opponentNickname = member.getId() != sender.getId()? sender.getNickname(): receiver.getNickname();

                    // 마지막 메세지 추출
                    OTOChat lastMessage = getLastMessage(chattingRoom);

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
