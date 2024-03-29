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

    public List<FacilityDto> getFacilityList(String building, String name){

        // 건물 기호 검색 여부 확인
        if (building == null) {
            building = "";
        }
        else {
            Optional<Building> buildingOptional = Enums.getIfPresent(Building.class, building);

            // 없는 건물 기호를 입려한 경우 에러 반환
            if (!buildingOptional.isPresent()) {
                throw new BaseException(BaseResponseStatus.INVALID_BUILDING_SYMBOL);
            }
            building = buildingOptional.get().getBuildingName();
        }

        // 이름 검색 여부 확인
        if (name == null) name = "";


        return facilityRepository.findByBuildingContainingAndNameContaining(building, name).stream()
                .map(facility -> {
                    return new FacilityDto(facility);
                }).collect(Collectors.toList());


//        return facilityRepository.findAll().stream()
//                .map(facility -> {
//                    return new FacilityDto(facility);
//                }).collect(Collectors.toList());

    }
}
