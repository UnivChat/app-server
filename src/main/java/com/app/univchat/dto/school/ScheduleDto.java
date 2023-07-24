package com.app.univchat.dto.school;

import com.app.univchat.domain.school.Schedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "학시일정")
public class ScheduleDto {

    @ApiModelProperty(name = "월", example = "8월")
    private String month;

    @ApiModelProperty(name = "날짜", example = "8.1 ~ 25")
    private String date;

    @ApiModelProperty(name = "이벤트", example = "휴학원 제출기간 (성심)")
    private String event;

    /**
     * Schedule 엔티티 반환
     * @return
     */
    public Schedule toEntity(){
        return Schedule.builder()
                .month(this.month)
                .date(this.date)
                .event(this.event)
                .build();

    }
}
