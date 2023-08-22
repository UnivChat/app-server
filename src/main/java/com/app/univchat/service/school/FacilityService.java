package com.app.univchat.service.school;

import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.repository.school.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<FacilityDto> getFacilityList(){
        return facilityRepository.findAll().stream()
                .map(facility -> {
                    return new FacilityDto(facility);
                }).collect(Collectors.toList());

    }
}
