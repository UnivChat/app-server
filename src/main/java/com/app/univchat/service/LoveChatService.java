package com.app.univchat.service;

import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.repository.LoveChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoveChatService {

    // MapperConfig에서 설정한 Mapper를 연결해주기 위함.
    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final LoveChatRepository loveChatRepository;

    /**
     *  연애상담 채팅 메시지 저장
     */
    @SneakyThrows
    @Transactional
    public void saveChat(ChatReq.LoveChatReq loveChatReq, String messageSendingTime) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = loveChatReq.getMemberNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);
        
        // 채팅 내역 저장
        loveChatRepository.save(loveChatReq.toEntity(sender, messageSendingTime));
    }

    /**
     *  연애상담 채팅 내역 조회
     */
    public ChatRes.LoveChatListRes getChattingList(int page, int size) {

        int all = loveChatRepository.findAll().size();
        //총 페이지수 0~maxPage, 0이 최신
        int maxPage = all / 10;

        if(page > maxPage) return null;

        if(page == -1){
            // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
            Pageable pageable = PageRequest.of(0, size,
                    Sort.by("messageSendingTime").descending());

            // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
            return new ChatRes.LoveChatListRes(maxPage, 0,
                    loveChatRepository.findAll(pageable)
                            .stream()
                            .map(chat -> modelMapper.map(chat, ChatRes.LoveChatRes.class))
                            .collect(Collectors.toList()));


        }

        // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").descending());

        // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
        return new ChatRes.LoveChatListRes(maxPage, page,
                loveChatRepository.findAll(pageable)
                        .stream()
                        .map(chat -> modelMapper.map(chat, ChatRes.LoveChatRes.class))
                        .collect(Collectors.toList()));

    }
}
