package com.app.univchat.chat;

import org.springframework.stereotype.Component;

/**
 * 채팅 저장 시 기본 키 발급
 */
@Component
public class ChatPrimaryKeyProvider {

    static private long sequence = 0;
    static private long limit = 100000000;

    /**
     * 채팅 DB 저장 기본 키 발급 메서드
     * @return key = messageSendingTIme + sequence
     */
    public String getKey(String messageSendingTime) {
        String date = messageSendingTime.replaceAll("[-: ]", "");

        sequence = (sequence + 1) % limit;
        String seqStr = String.format("%09d", sequence);

        return date+seqStr;
    }

}
