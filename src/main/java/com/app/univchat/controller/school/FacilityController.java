package com.app.univchat.controller.school;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.school.FacilityDto;
import com.app.univchat.service.school.FacilityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "school", description = "학교 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/school")
public class FacilityController {

    private final FacilityService facilityService;

    @Tag(name = "school", description = "학교 정보 관련 API")
    @ApiOperation(value = "편의시설 전체 조회 API", notes = "건물별 편의시설 조회 시 건물 번호 검색 \n\n " +
            "[K: 김수환관, N: 니콜스관, V: 비루투스관, D: 다솔관, B: 학생미래인재관, T: 미카엘관, L: 베리타스관, A: 안드레아관, BA: 밤비노관")
    @GetMapping("/facility")
    public BaseResponse<List<FacilityDto>> getFacilityList(@RequestParam(required = false)String building) {

        List<FacilityDto> result = facilityService.getFacilityList(building);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

}
