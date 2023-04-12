package com.app.univchat.controller;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.dto.MemberReq;
import com.app.univchat.dto.MemberRes;
import com.app.univchat.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.app.univchat.base.BaseResponseStatus.SUCCESS;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final EmailService emailService;

    @SneakyThrows
    @PostMapping("/email/verified")
    public MemberRes.EmailAuthRes memberEmailVerified(@RequestBody MemberReq.EmailAuthReq emailAuthReq) {
        MemberRes.EmailAuthRes emailAuthRes = emailService.sendEmail(emailAuthReq.getEmail());

        return emailAuthRes;
    }

}
