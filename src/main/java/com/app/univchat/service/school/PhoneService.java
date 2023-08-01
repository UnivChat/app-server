package com.app.univchat.service.school;

import com.app.univchat.dto.school.PhoneDto;
import com.app.univchat.repository.school.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneRepository phoneRepository;

    public List<PhoneDto> getPhoneList(){
        return phoneRepository.findAll().stream()
                .map(phone -> {
                    return new PhoneDto(phone);
                })
                .collect(Collectors.toList());
    }
}
