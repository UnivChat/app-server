package com.app.univchat.controller;

import com.app.univchat.base.BaseException;
import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.InquiryDto;
import com.app.univchat.service.EmailService;
import com.app.univchat.service.InquiryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
@Tag(name = "inquiry", description = "문의 관련 API")
public class InquiryController {

    private final EmailService emailService;
    private final InquiryService inquiryService;

    @Tag(name = "inquiry")
    @ApiOperation(value = "문의 접수 API", notes = "문의 사항을 저장하고 문의 접수 성공 시 사용자에게 안내 메일을 보냅니다.")
    @PostMapping("/register")
    public BaseResponse<String> acceptInquiry(@RequestBody InquiryDto inquiry) {

        try {
            emailService.sendInquirySuccessMail(inquiry.getReceiverEmail(), inquiry.getContent());
            inquiryService.saveEmailAndInquiry(inquiry.getReceiverEmail(), inquiry.getContent());

            return BaseResponse.ok(BaseResponseStatus.SUCCESS, "접수하신 문의를 성공적으로 등록되었습니다. 입력한 이메일의 메일함을 확인하세요.");
        } catch (MessagingException e) {
            throw new BaseException(BaseResponseStatus.USER_FAILED_TO_ACCEPT_INQUIRY);
        }
    }
}
