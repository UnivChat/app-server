package com.app.univchat.service.chat;

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
import com.app.univchat.service.CipherService;
import com.app.univchat.service.MemberService;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OTOChatService {

    // MapperConfig에서 설정한 Mapper를 연결해주기 위함.
    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final CipherService cipherService;
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
        String receiverNickname = otoChatRoomReq.getReceiverNickname();
        Optional<Member> receiver = memberService.getMember(receiverNickname);

        // 송신자 - 수신자 쌍이 이미 존재하면 채팅방 개설 불가
        ChatRes.OTOChatRoomRes otoChatRoomRes = new ChatRes.OTOChatRoomRes();

        // 이미 채팅방 존재하면 해당 채팅방 id return
        Optional<OTOChatRoom> foundRoom = null;
        if(otoChatRoomRepository.findBySenderAndReceiver(sender.get(),receiver.get())!=null) {
            foundRoom = otoChatRoomRepository.findBySenderAndReceiver(sender.get(),receiver.get());
            if(foundRoom.isEmpty()) {
                foundRoom = otoChatRoomRepository.findBySenderAndReceiver(receiver.get(),sender.get());
            }
        }
        if(foundRoom.isEmpty()) {  // 채팅방 개설
            otoChatRoomRepository.save(otoChatRoomReq.toEntity(sender,receiver));
            foundRoom = otoChatRoomRepository.findBySenderAndReceiver(sender.get(),receiver.get());
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
        Optional<OTOChatRoom> room = otoChatRoomRepository.findByRoomId(roomId);

        // 채팅 내역 저장
        otoChatRepository.save(((ChatReq.OTOChatReq)cipherService.encryptChat(otoChatReq)).toEntity(room, sender ,messageSendingTime));

    }

    /**
     *  1:1 채팅 내역 조회
     */
    public ChatRes.OTOChatListRes getChattingList(Long roomId, int page) {
        OTOChatRoom room = otoChatRoomRepository.findByRoomId(roomId).orElseThrow(
                () -> new BaseException(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR));

        int size = 10;

        int all = otoChatRepository.findByRoom(room).size();

        int maxPage = all / size;

        if(all%size == 0 && maxPage > 0) maxPage--;

        List<OTOChat> page2 = new ArrayList<>();
        if (page == -1) {
            page = maxPage;

            if (all % size != 0 && page > 0) {
                PageRequest pageable = PageRequest.of(page - 1, size,
                        Sort.by("messageSendingTime").ascending());
                page2 = otoChatRepository.findByRoom(room, pageable).toList();
            }
        }

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").ascending());

        List<OTOChat> page1 = otoChatRepository.findByRoom(room, pageable).toList();
        if (!page2.isEmpty()) {
            page1 = Stream.concat(page1.stream(), page2.stream()).collect(Collectors.toList());
        }
        List<ChatRes.OTOChatRes> chattingList = page1.stream()
                .map(chat -> modelMapper.map(chat, ChatRes.OTOChatRes.class))
                .peek(chat -> chat.setMessageContent(cipherService.decryptChat(chat).getMessageContent()))
                .sorted((o1, o2) -> o2.getMessageSendingTime().compareTo(o1.getMessageSendingTime()))
                .collect(Collectors.toList());

        return new ChatRes.OTOChatListRes(maxPage, (page2.isEmpty() ? page : page - 1), chattingList);
    }

    public boolean checkVisible(Long roomId) {
        // 현재 참여 채팅방 객체
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);

        // 모든 유저가 채팅방에 남아있을 경우 true
        if(room.get().getVisible() == OTOChatVisible.ALL)
            return true;
        else
            return false;
    }

    /**
     *  1:1 채팅방 나가기
     */
    public String exitChatRoom(Long roomId, Member member) {
        // 채팅방 나가는 회원 memberId
        Long memberId = member.getId();

        // 현재 참여 채팅방 객체
        OTOChatRoom chatRoom = otoChatRoomRepository.findByRoomId(roomId).get();

        // 채팅방 sender memberId
        Long senderId = chatRoom.getSender().getId();   // sender memberId

        // sender 가 나갈 경우
        if(senderId == memberId) {
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
        // 채팅방 나가는 회원 memberId
        Long memberId = member.getId();

        // 현재 참여 채팅방 객체
        Optional<OTOChatRoom> room=otoChatRoomRepository.findByRoomId(roomId);

        // 채팅 내역 삭제하기
        otoChatRepository.deleteByRoom(room);
        otoChatRoomRepository.deleteByRoomId(roomId);

        return "채팅방이 삭제되었습니다.";
    }

    /**
     * 1:1 채팅방 ID를 통해 가장 최근 메세지 조회
     */
    public ChatRes.OTOChatRes getLastMessage(OTOChatRoom room) {
        Optional<OTOChat> message = otoChatRepository.findTop1ByRoomOrderByMessageSendingTimeDesc(room);
        if (message.isPresent()) {
            ChatRes.OTOChatRes lastMessage = modelMapper.map(message.get(), ChatRes.OTOChatRes.class);
            return (ChatRes.OTOChatRes)cipherService.decryptChat(lastMessage);
        }
        else return null;
    }

    /**
     *  사용자별 1:1 채팅방 목록 조회
     */
    public List<ChatRes.OTOChatRoomRes> getChattingRoomList(Member member) {

        // 사용자 ID를 통해 송신자 혹은 수신자의 ID와 같다면 해당 1:1 채팅방 조회
        List<OTOChatRoom> result = otoChatRoomRepository.findBySenderOrReceiver(member, member);

        // visible에 따른 필터링
        if(result != null) {
            result = result.stream().filter(chattingRoom ->
                            chattingRoom.getVisible() == OTOChatVisible.ALL ||
                            (chattingRoom.getVisible() == OTOChatVisible.SENDER && chattingRoom.getSender().getId() == member.getId()) ||
                            (chattingRoom.getVisible() == OTOChatVisible.RECEIVER && chattingRoom.getReceiver().getId() == member.getId()))
                           .collect(Collectors.toList());
        }

        // 조회한 1:1 채팅방 목록을 이용하여 응답 객체와 매핑
        List<ChatRes.OTOChatRoomRes> chattingRoomList = result.stream()
                // 채팅방별 매핑
                .map(chattingRoom -> {
                    // 상대방 닉네임 추출
                    Member sender = chattingRoom.getSender();
                    Member receiver = chattingRoom.getReceiver();
                    String opponentNickname = member.getId() != sender.getId()? sender.getNickname(): receiver.getNickname();

                    // 마지막 메세지 추출
                    ChatRes.OTOChatRes lastMessage = getLastMessage(chattingRoom);

                    //반환 객체 생성
                    ChatRes.OTOChatRoomRes chatRes = new ChatRes.OTOChatRoomRes(chattingRoom.getRoomId(), opponentNickname);
                    if(lastMessage != null) {
                        chatRes.setLastMessageContent(lastMessage.getMessageContent());
                        chatRes.setLastMessageSendingTime(lastMessage.getMessageSendingTime());
                    }
                    return chatRes;
                })
                // 마지막 메세지를 기준으로 최신순 정렬
                .sorted(Comparator.comparing(ChatRes.OTOChatRoomRes::getLastMessageSendingTime).reversed())
                // 매핑한 결과를 리스트로 반환
                .collect(Collectors.toList());

        return chattingRoomList;
    }

}