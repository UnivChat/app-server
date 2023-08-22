package com.app.univchat.batch.writer;

import com.app.univchat.domain.school.Facility;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.repository.school.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class CsvFacilityWriter implements ItemWriter<FacilityDto> {

    private final FacilityRepository facilityRepository;


    @Override
    public void write(List<? extends FacilityDto> items) throws Exception {
        List<Facility> facilityList = new ArrayList<>();

        items.forEach(getFacilityDto -> {
            Facility facility = getFacilityDto.toEntity();
            facilityList.add(facility);
        });

        facilityRepository.saveAll(facilityList);

    }
}
