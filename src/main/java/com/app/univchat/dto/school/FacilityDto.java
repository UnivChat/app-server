package com.app.univchat.dto.school;

import com.app.univchat.domain.school.Facility;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "편의시설")
public class FacilityDto {

    @ApiModelProperty(name = "건물", example = "k 김수환관")
    private String building;

    @ApiModelProperty(name = "이름", example = "세탁소")
    private String name;

    @ApiModelProperty(name = "위치", example = "1층")
    private String location;

    @ApiModelProperty(name = "운영시간1", example = "평일 10:00 - 17:00")
    private String time1;

    @ApiModelProperty(name = "운영시간2", example = "토 10:00 - 14:00")
    private String time2;

    @ApiModelProperty(name = "전화번호", example = "010-2000-6252")
    private String phone;

    public Facility toEntity() {
        return Facility.builder()
                .building(this.building)
                .name(this.name)
                .location(this.location)
                .time1(this.time1)
                .time2(this.time2)
                .phone(this.phone)
                .build();
    }

    public FacilityDto(Facility facility) {
        this.building = facility.getBuilding();
        this.location = facility.getLocation();
        this.name = facility.getName();
        this.time1 = (facility.getTime1() != null ? facility.getTime1() : "");
        this.time2 = (facility.getTime2() != null ? facility.getTime2() : "");
        this.phone = (facility.getPhone() != null ? facility.getPhone() : "");

    }
}
