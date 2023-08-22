package com.app.univchat.batch.writer;

import com.app.univchat.domain.school.Phone;
import com.app.univchat.dto.school.PhoneDto;
import com.app.univchat.repository.school.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class CsvPhoneWriter implements ItemWriter<PhoneDto> {

    private final PhoneRepository phoneRepository;


    @Override
    public void write(List<? extends PhoneDto> items) throws Exception {
        List<Phone> phoneList = new ArrayList<>();

        items.forEach(getPhoneDto -> {
            Phone phone = getPhoneDto.toEntity();
            phoneList.add(phone);
        });

        phoneRepository.saveAll(phoneList);

    }
}
