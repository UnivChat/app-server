package com.app.univchat.controller.school;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.school.ScheduleDto;
import com.app.univchat.service.school.ScheduleService;
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
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Tag(name = "school")
    @ApiOperation(value = "학사일정 전체 조회 API")
    @GetMapping("/schedule")
    public BaseResponse<List<ScheduleDto>> getScheduleList() {

        List<ScheduleDto> result = scheduleService.getScheduleList();

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }

}
