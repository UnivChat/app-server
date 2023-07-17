package com.app.univchat.batch;

import com.app.univchat.domain.ClassRoom;
import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 전달받은 데이터 DB에 저장
 */
@Configuration
@RequiredArgsConstructor
public class CsvWriter implements ItemWriter<ClassRoomDto> {
    private final ClassRoomRepository classRoomRepository;
    @Override
    public void write(List<? extends ClassRoomDto> items) throws Exception {
        List<ClassRoom> classRoomList = new ArrayList<>();

        items.forEach(getClassRoomDto -> {
            ClassRoom classRoom = getClassRoomDto.toEntity();
            classRoomList.add(classRoom);
        });

        classRoomRepository.saveAll(classRoomList);
    }
}
