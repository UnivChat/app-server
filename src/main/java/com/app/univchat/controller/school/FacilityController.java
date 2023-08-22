package com.app.univchat.controller.school;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.service.school.FacilityService;
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
public class FacilityController {

    private final FacilityService facilityService;

    @Tag(name = "school", description = "학교 정보 관련 API")
    @ApiOperation(value = "편의시설 전체 조회 API")
    @GetMapping("/facility")
    public BaseResponse<List<FacilityDto>> getFacilityList() {

        List<FacilityDto> result = facilityService.getFacilityList();

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

}
