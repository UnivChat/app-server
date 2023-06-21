package com.app.univchat.service.chat;

import com.app.univchat.base.BaseException;
import com.app.univchat.domain.LiveChat;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.app.univchat.base.BaseResponseStatus.CHAT_OVERFLOW_THE_RANGE;

@Service
@RequiredArgsConstructor
public class LiveChatService {

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
    public ChatRes.LiveChatListRes getChattingList(int page, int size) {

        int all = liveChatRepository.findAll().size();

        //총 페이지 수
        int maxPage = all / size;

        //총 채팅 수 나누어 떨어지면 마지막 페이지에 아무것도 없음(페이지는 0부터 시작하기 때문)
        if(all%size == 0 && maxPage > 0) maxPage--;

        if(page > maxPage || page <= -2) throw new BaseException(CHAT_OVERFLOW_THE_RANGE);

        List<LiveChat> page2 = new ArrayList<>();
        if(page == -1){
            //초기에 채팅 리스트 불러올 때
            //-1로 접근하면 무조건 최신 10개
            page = maxPage;

            //최신 채팅 페이지네이션할 때 10개 미만이면 다음 페이지도 함께 조회
            if (all%size != 0 && page > 0) {
                PageRequest pageable = PageRequest.of(page-1, size,
                        Sort.by("messageSendingTime").ascending());
                page2 = liveChatRepository.findAll(pageable).toList();
            }
        }

        //pageable 객체 생성
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("messageSendingTime").ascending());

        // ChatRes.LiveChatRes으로 변환하여 반환
        List<LiveChat> page1 = liveChatRepository.findAll(pageable).toList();
        if (!page2.isEmpty()) {
            page1 = Stream.concat(page1.stream(), page2.stream()).collect(Collectors.toList());
        }
        List<ChatRes.LiveChatRes> chattingList = page1.stream()
                .map(chat -> modelMapper.map(chat, ChatRes.LiveChatRes.class))
                .peek(chat -> chat.setMessageContent(cipherService.decryptChat(chat).getMessageContent())) //채팅 복호화
                .sorted((o1, o2) -> o2.getMessageSendingTime().compareTo(o1.getMessageSendingTime())) //채팅 보낸 시간으로 정렬
                .collect(Collectors.toList());

        return new ChatRes.LiveChatListRes(maxPage, (page2.isEmpty()?page:page-1), chattingList);
    }
}
