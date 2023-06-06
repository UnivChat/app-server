package com.app.univchat.service;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.repository.LiveChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiveChatService {

    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final CipherService cipherService;
    private final LiveChatRepository liveChatRepository;

    /**
     * 라이브 채팅 저장
     */
    @SneakyThrows
    @Transactional
    public void saveChat(ChatReq.LiveChatReq liveChatReq, String messageSendTime) {
        // nickname으로 member 찾기
        String senderNickname = liveChatReq.getMemberNickname();
        Member sender = memberService.getMember(senderNickname).orElseThrow(
//                () -> new BaseException(BaseResponseStatus.USER_NOT_EXIST_NICKNAME_ERROR)
                () ->  new Exception("존재하지 않는 회원입니다.")
        );

        // 채팅 내역 저장
        liveChatRepository.save(((ChatReq.LiveChatReq)(cipherService.encryptChat(liveChatReq))).toEntity(sender, messageSendTime));
    }

    /**
     * 라이브 채팅 내역 리스트 조회
     */
    public List<ChatRes.LiveChatRes> getChattingList(int page, int size) {
        
        //pageable 객체 생성
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").descending());

        // ChatRes.LiveChatRes으로 변환하여 반환
        List<ChatRes.LiveChatRes> chattingList = liveChatRepository.findAll(pageable)
                .stream()
                .map(chat -> modelMapper.map(chat, ChatRes.LiveChatRes.class))
                .collect(Collectors.toList());

        for(ChatRes.LiveChatRes chatting : chattingList) {
            chatting.setMessageContent(cipherService.decryptChat(chatting).getMessageContent());
        }

        return chattingList;
    }
}
