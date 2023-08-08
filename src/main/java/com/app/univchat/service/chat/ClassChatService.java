package com.app.univchat.service.chat;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.ClassChat;
import com.app.univchat.domain.ClassChatMember;
import com.app.univchat.domain.ClassRoom;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ClassChatReq;
import com.app.univchat.dto.ClassChatRes;
import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.repository.ClassChatMemberRepository;
import com.app.univchat.repository.ClassChatRepository;
import com.app.univchat.repository.ClassRoomRepository;
import com.app.univchat.service.CipherService;
import com.app.univchat.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ClassChatService {

    private final MemberService memberService;
    private final CipherService cipherService;
    private final ModelMapper modelMapper;
    private final ClassRoomRepository classRoomRepository;
    private final ClassChatRepository classChatRepository;
    private final ClassChatMemberRepository classChatMemberRepository;

    private static final int size = 50;

    public List<ClassRoomDto> getClassRoomList(int page){
        //pageable 객체 생성
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("className").ascending()
        );

        //조회 및 DTO로 매핑
        List<ClassRoomDto> result = classRoomRepository.findAll(pageable).stream()
                .map(classRoom -> {
                    return new ClassRoomDto(classRoom);
                })
                .collect(Collectors.toList());

        return result;

    }

    // 클래스 채팅 메시지 저장
    @SneakyThrows
    @Transactional
    public void saveChat(String classNumber, ClassChatReq.Chat classChatReq, String messageSendingTime) {

        String senderNickname = classChatReq.getMemberNickname();

        Optional<Member> sender = memberService.getMember(senderNickname);

        Optional<ClassRoom> classRoom = classRoomRepository.findById(classNumber);

        classChatRepository.save(((ClassChatReq.Chat)cipherService.encryptChat(classChatReq)).toEntity(classRoom, sender ,messageSendingTime));

    }

    // 클래스 채팅 내역 조회
    public ClassChatRes.ChatList getChattingList(String classNumber, int page, Member member) {

        ClassRoom classRoom = classRoomRepository.findById(classNumber).orElseThrow(
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
        List<ClassChatRes.Chat> chattingList = page1.stream()
                .map(chat -> modelMapper.map(chat, ClassChatRes.Chat.class))
                .peek(chat -> chat.setMessageContent(cipherService.decryptChat(chat).getMessageContent()))
                .sorted((o1, o2) -> o2.getMessageSendingTime().compareTo(o1.getMessageSendingTime()))
                .collect(Collectors.toList());

        updateLastAccessTime(classNumber, member);

        return new ClassChatRes.ChatList(maxPage, (page2.isEmpty() ? page : page - 1), chattingList);
    }

    // 사용자별 클래스 채팅 목록 조회
    public List<ClassChatRes.ChattingRoom> getChattingRoomList(Member member) {

        List<ClassChatMember> accessMember = classChatMemberRepository.findByMember(member);

        List<ClassRoomDto> classList = accessMember
                .stream()
                .map(classRoomMember -> modelMapper.map(classRoomRepository.findById(classRoomMember.getClassRoom().getClassNumber()).get(), ClassRoomDto.class))
                .collect(Collectors.toList());

        return classList
                .stream()
                .map((classRoom) -> {
                    Optional<ClassChat> classChat = classChatRepository.findTopByClassRoomOrderByMessageSendingTimeDesc(classRoom.toEntity());
                    Optional<ClassChatMember> chatMember = classChatMemberRepository.findByClassRoomClassNumberAndMember(classRoom.getClassNumber(), member);

                    if(classChat.isPresent() && chatMember.isPresent()) {

                        return ClassChatRes.ChattingRoom
                                .builder()
                                .classRoom(classRoom)
                                .lastMessageSendingTime(classChat.get().getMessageSendingTime())
                                .numberOfUnreadMessage(classChatRepository.countByMessageSendingTimeGreaterThan(chatMember.get().getLastAccessTime()))
                                .build();

                    } else {

                        // 채팅방에 채팅이 없는 경우
                        return ClassChatRes.ChattingRoom
                                .builder()
                                .classRoom(classRoom)
                                .lastMessageSendingTime("")
                                .numberOfUnreadMessage(0)
                                .build();
                    }

                })
                .collect(Collectors.toList());

    }

    // 사용자의 클래스 목록에서 추가
    @Transactional
    public String insertChattingRoomMemberByClassNumber(String classNumber, Member member) {

        Optional<ClassRoom> classRoom = classRoomRepository.findById(classNumber);

        if(classRoom.isPresent()) {
            ClassChatMember classChatRoomMember = ClassChatMember
                    .builder()
                    .classRoom(classRoom.get())
                    .member(member)
                    .lastAccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()))
                    .build();

            if(classChatMemberRepository.findByClassRoomClassNumberAndMember(classNumber, member).isEmpty()) {

                classChatMemberRepository.save(classChatRoomMember);

                return "사용자의 클래스 목록에서 해당 클래스를 추가했습니다.";

            } else {

                // 사용자의 클래스 목록에 이미 있는 경우
                throw new BaseException(BaseResponseStatus.CLASS_ALREADY_EXIST);

            }

        } else {

            // 해당 클래스가 없는 경우
            throw new BaseException(BaseResponseStatus.CLASS_NOT_FOUND);

        }
    }

    // 사용자의 클래스 목록에서 삭제
    @Transactional
    public String deleteChattingRoomMemberByClassNumber(String classNumber, Member member) {

        Optional<ClassRoom> classRoom = classRoomRepository.findById(classNumber);

        if(classRoom.isPresent()) {

            if(classChatMemberRepository.findByClassRoomClassNumberAndMember(classNumber, member).isPresent()) {

                classChatMemberRepository.deleteByClassRoomClassNumberAndMember(classNumber, member);

                return "사용자의 클래스 목록에서 해당 클래스를 삭제했습니다.";

            } else {

                // 사용자의 클래스 목록에 없는 경우
                throw new BaseException(BaseResponseStatus.CLASS_NOT_FOUND_IN_USER_LIST);

            }

        } else {

            // 클래스 목록에 없는 경우
            throw new BaseException(BaseResponseStatus.CLASS_NOT_FOUND);

        }
    }

    // 마지막 접속 시간 업데이트
    private void updateLastAccessTime(String classNumber, Member member) {

        Optional<ClassChatMember> classChatMember = classChatMemberRepository.findByClassRoomClassNumberAndMember(classNumber, member);

        if(classChatMember.isPresent()) {
            ClassChatMember accessMember = ClassChatMember
                    .builder()
                    .classRoomMemberId(classChatMember.get().getClassRoomMemberId())
                    .classRoom(classChatMember.get().getClassRoom())
                    .member(classChatMember.get().getMember())
                    .lastAccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()))
                    .build();

            classChatMemberRepository.save(accessMember);
        } else {

            // 해당 맴버가 채팅방에 입장하지 않은 경우
            throw new BaseException(BaseResponseStatus.USER_NO_EXIST_CHATTING_ROOM);

        }

    }
}
