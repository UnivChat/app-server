package com.app.univchat.config;

import com.app.univchat.domain.DormChat;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.ChatRes;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        // join column인 memberId를 매핑하기 위한 설정
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(DormChat.class, ChatRes.DormChatRes.class)
                .addMapping(chat -> chat.getMember().getNickname(), ChatRes.DormChatRes::setMemberNickname);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private String mappingMemberToMemberNickname(Member member) {
        return member.getNickname();
    }
}