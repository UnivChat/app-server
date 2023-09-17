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
    @ApiOperation(value = "편의시설 조회 API", notes = "조회 방법 3가지\n\n" +
            "1. 전체 조회 - path에 아무것도 넣지 않고 조회 (ex. /school/facility) \n\n" +
            "2. 건물별 조회 - path에 건물 기호 넣고 요청 (ex. /school/facility/K) \n\n" +
            "   [K: 김수환관, N: 니콜스관, V: 비루투스관, D: 다솔관, B: 학생미래인재관, T: 미카엘관, L: 베리타스관, A: 안드레아관, BA: 밤비노관] \n\n" +
            "3. 건물 + 이름 조회 - path에 건물 기호와 이름 넣고 요청 (ex. /school/facility/K/카페멘사)"
            )
    @GetMapping(value = {"/facility", "/facility/{building}", "/facility/{building}/{name}"})
    public BaseResponse<List<FacilityDto>> getFacilityList(@PathVariable(name = "building", required = false)String building,
                                                           @PathVariable(name = "name", required = false)String name) {

        List<FacilityDto> result = facilityService.getFacilityList(building, name);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

}
