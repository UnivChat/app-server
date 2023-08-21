package com.app.univchat.service;

import com.app.univchat.domain.Inquiry;
import com.app.univchat.dto.InquiryDto;
import com.app.univchat.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public void saveEmailAndInquiry(String email, String inquiry) {
        inquiryRepository.save(new InquiryDto().toEntity(email, inquiry));
    }
}
