package com.app.univchat.service.school;

import com.app.univchat.dto.school.ScheduleDto;
import com.app.univchat.repository.school.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleDto> getScheduleList() {
        return scheduleRepository.findAll().stream()
                .map(schedule -> {
                    return new ScheduleDto(schedule);
                })
                .collect(Collectors.toList());
    }

}
