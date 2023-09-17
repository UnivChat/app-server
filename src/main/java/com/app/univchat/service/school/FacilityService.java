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

    public List<FacilityDto> getFacilityList(String building){
        //TODO: 필터링 추가하기

        // 건물 기호로 검색한 경우
        if (building != null) {
            Optional<Building> buildingOptional = Enums.getIfPresent(Building.class, building);

            // 없는 건물 기호를 입려한 경우 에러 반환
            if (!buildingOptional.isPresent()) {
                new BaseException(BaseResponseStatus.INVALID_BUILDING_SYMBOL);
            }
            building = buildingOptional.get().getBuildingName();
        } else building = "";

        return facilityRepository.findByBuildingContaining(building).stream()
                .map(facility -> {
                    return new FacilityDto(facility);
                }).collect(Collectors.toList());


//        return facilityRepository.findAll().stream()
//                .map(facility -> {
//                    return new FacilityDto(facility);
//                }).collect(Collectors.toList());

    }
}
