package com.app.univchat.service.school;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.school.Building;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.repository.school.FacilityRepository;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<FacilityDto> getFacilityList(String name){

        if (name == null) name = "";

        return facilityRepository.findByNameContaining(name).stream()
                .map(facility -> {
                    return new FacilityDto(facility);
                }).collect(Collectors.toList());


//        return facilityRepository.findAll().stream()
//                .map(facility -> {
//                    return new FacilityDto(facility);
//                }).collect(Collectors.toList());

    }
}
