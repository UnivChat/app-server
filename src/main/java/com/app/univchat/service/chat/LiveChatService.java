package com.app.univchat.service.chat;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.repository.LiveChatRepository;
import com.app.univchat.service.CipherService;
import com.app.univchat.service.MemberService;
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

    //오래된 순으로 정렬 후
    //마지막 페이지를 계산 후
    //마지막 페이지를 가져옴
    //요청 자체를 -1로 받고 반환에 페이지 마지막 번호 추가
    //지금 82페이지니까
    //총 9페이지
    //실제 9페이지는 9로 요청

    public ChatRes.LiveChatListRes getChattingList(int page, int size) {
        int all = liveChatRepository.findAll().size();
        //총 페이지수 0~maxPage, 0이 최신
        int maxPage = all / 10;

        if(page > maxPage) return null;

        if(page == -1){
            //초기에 채팅 리스트 불러올 때
            //-1로 접근하면 무조건 최신 10개
            page = 0;
        }


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

        return new ChatRes.LiveChatListRes(maxPage, page, chattingList);

    }
}
