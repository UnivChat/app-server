package com.app.univchat.service;

import com.app.univchat.domain.DormChat;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatReq;
import com.app.univchat.dto.ChatRes;
import com.app.univchat.repository.DormChatRepository;
import com.app.univchat.repository.MemberRepository;
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
public class DormChatService {

    // MapperConfig에서 설정한 Mapper를 연결해주기 위함.
    @Autowired
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final DormChatRepository dormChatRepository;

    /**
     *  기숙사 채팅 메시지 저장
     */
    @SneakyThrows
    @Transactional
    public void saveChat(ChatReq.DormChatReq dormChatReq, String messageSendingTime) {

        // nickname으로 송신자 member 객체 획득
        String senderNickname = dormChatReq.getMemberNickname();
        Optional<Member> sender = memberService.getMember(senderNickname);
        
        // 채팅 내역 저장
        dormChatRepository.save(dormChatReq.toEntity(sender, messageSendingTime));
    }

    /**
     *  기숙사 채팅 내역 조회
     */
    public ChatRes.DormChatListRes getChattingList(int page, int size) {

        int all = dormChatRepository.findAll().size();
        //총 페이지수 0~maxPage, 0이 최신
        int maxPage = all / 10;

        if(page > maxPage) return null;

        if(page == -1){
            //초기에 채팅 리스트 불러올 때
            //-1로 접근하면 무조건 최신 10개
            page = 0;
        }


        // page는 요청하는 곳에 맞게, 한 번의 요청에는 10개의 채팅, 시간 내림차순으로 정렬.
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").descending());

        // pagenation 한 채팅 목록을 modleMapper로 변환하여 반환
        return new ChatRes.DormChatListRes(maxPage, page,
                dormChatRepository.findAll(pageable)
                        .stream()
                        .map(chat -> modelMapper.map(chat, ChatRes.DormChatRes.class))
                        .collect(Collectors.toList()));

    }
}
