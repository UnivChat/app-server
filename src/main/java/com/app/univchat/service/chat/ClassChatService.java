package com.app.univchat.service.chat;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.chat.OTOChatVisible;
import com.app.univchat.domain.*;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.repository.ClassChatRepository;
import com.app.univchat.repository.ClassChatRoomMemberRepository;
import com.app.univchat.repository.ClassChatRoomRepository;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.CipherService;
import com.app.univchat.service.MemberService;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClassChatService {

    private ModelMapper modelMapper;
    private MemberService memberService;
    private CipherService cipherService;
    private ClassChatRepository classChatRepository;
    private ClassChatRoomRepository classChatRoomRepository;
    private ClassChatRoomMemberRepository classChatRoomMemberRepository;

    /**
     *  클래스 채팅 메시지 저장
     */
    @SneakyThrows
    @Transactional
    public void saveChat(String roomId, ChatReq.ClassChatReq classChatReq, String messageSendingTime) {

            String senderNickname = classChatReq.getMemberNickname();

            Optional<Member> sender = memberService.getMember(senderNickname);

            Optional<ClassChatRoom> classRoom = classChatRoomRepository.findByClassNumber(roomId);

            classChatRepository.save(((ChatReq.ClassChatReq)cipherService.encryptChat(classChatReq)).toEntity(classRoom, sender ,messageSendingTime));

    }

    /**
     *  클래스 채팅 내역 조회
     */
    public ChatRes.ClassChatListRes getChattingList(String roomId, int page) {

        ClassChatRoom classRoom = classChatRoomRepository.findByClassNumber(roomId).orElseThrow(
                () -> new BaseException(BaseResponseStatus.CHATTING_NOT_EXIST_ROOM_ERROR));

        int size = 10;

        int all = classChatRepository.findByClassRoom(classRoom).size();

        int maxPage = all / size;

        if(all%size == 0 && maxPage > 0) maxPage--;

        List<ClassChat> page2 = new ArrayList<>();
        if (page == -1) {
            page = maxPage;

            if (all % size != 0 && page > 0) {
                PageRequest pageable = PageRequest.of(page - 1, size,
                        Sort.by("messageSendingTime").ascending());
                page2 = classChatRepository.findByClassRoom(classRoom, pageable).toList();
            }
        }

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").ascending());

        List<ClassChat> page1 = classChatRepository.findByClassRoom(classRoom, pageable).toList();
        if (!page2.isEmpty()) {
            page1 = Stream.concat(page1.stream(), page2.stream()).collect(Collectors.toList());
        }
        List<ChatRes.ClassChatRes> chattingList = page1.stream()
                .map(chat -> modelMapper.map(chat, ChatRes.ClassChatRes.class))
                .peek(chat -> chat.setMessageContent(cipherService.decryptChat(chat).getMessageContent()))
                .sorted((o1, o2) -> o2.getMessageSendingTime().compareTo(o1.getMessageSendingTime()))
                .collect(Collectors.toList());

        return new ChatRes.ClassChatListRes(maxPage, (page2.isEmpty() ? page : page - 1), chattingList);
    }

    /**
     *  사용자별 클래스 채팅 목록 조회
     */
    public List<ChatRes.ClassChatRoomRes> getChattingRoomList(Member member) {

        List<ClassChatRoomMember> result = classChatRoomMemberRepository.findByMember(member);

        List<ChatRes.ClassChatRoomRes> chattingRoomList = result
                .stream()
                .map(classRoomMember -> modelMapper.map(classChatRoomRepository.findByClassNumber(classRoomMember.getClassRoom().getClassNumber()), ChatRes.ClassChatRoomRes.class))
                .collect(Collectors.toList());

        return chattingRoomList;

    }

    /**
     * 클래스 전체 목록 조회
     * */
    public ChatRes.ClassChatRoomListRes getChattingRoomListAll(int page) {

        int size = 10;

        int all = classChatRepository.findAll().size();

        int maxPage = all / size;

        if(all%size == 0 && maxPage > 0) maxPage--;

        PageRequest pageable = PageRequest.of(page, size);

        List<ChatRes.ClassChatRoomRes> chattingList = classChatRoomRepository.findAll(pageable).toList()
                .stream()
                .map(chat -> modelMapper.map(chat, ChatRes.ClassChatRoomRes.class))
                .collect(Collectors.toList());

        return new ChatRes.ClassChatRoomListRes(maxPage, page, chattingList);
    }

    /**
     * 사용자의 클래스 목록에서 추가
     * */
    public String insertChattingRoomMemberByClassNumber(String roomId, Member member) {

        ClassChatRoom classRoom = classChatRoomRepository.findByClassNumber(roomId).get();

        if(classRoom.getClassNumber().isEmpty()) {

            return "해당 클래스가 존재하지 않습니다.";
        }

        ClassChatRoomMember classChatRoomMember = ClassChatRoomMember
                .builder()
                .classRoom(classRoom)
                .member(member)
                .build();

        classChatRoomMemberRepository.save(classChatRoomMember);

        return "사용자의 클래스 목록에서 해당 클래스를 추가했습니다.";
    }


    /**
     * 사용자의 클래스 목록에서 삭제
     * */
    public String deleteChattingRoomMemberByClassNumber(String roomId, Member member) {

        ClassChatRoom classRoom = classChatRoomRepository.findByClassNumber(roomId).get();

        if(classRoom.getClassNumber().isEmpty()) {

            return "해당 클래스가 존재하지 않습니다.";
        }

        classChatRoomMemberRepository.deleteByClassRoomClassNumberAndMember(roomId, member);

        return "사용자의 클래스 목록에서 해당 클래스를 삭제했습니다.";
    }
}
