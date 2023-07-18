package com.app.univchat.service.chat;

import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassChatService {

    private final ClassRoomRepository classRoomRepository;
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

}
