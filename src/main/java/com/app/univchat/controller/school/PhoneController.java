package com.app.univchat.controller.school;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.school.PhoneDto;
import com.app.univchat.service.school.PhoneService;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "school", description = "학교 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/school")
public class PhoneController {

    private final PhoneService phoneService;

    @Tag(name = "school", description = "학교 정보 관련 API")
    @ApiOperation(value = "연락망 전체 조회 API")
    @GetMapping("/phone")
    public BaseResponse<List<PhoneDto>> getPhoneList(){

        List<PhoneDto> result = phoneService.getPhoneList();

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }
}
