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

    @ApiModelProperty(name = "이름", example = "카페 멘사")
    private String name;

    @ApiModelProperty(name = "위치", example = "1층")
    private String location;

    @ApiModelProperty(name = "운영시간", example = "(조식)08:00 – 09:00")
    private String time;

    @ApiModelProperty(name = "전화번호", example = "02-2632-7000")
    private String phone;

    public Facility toEntity() {
        return Facility.builder()
                .building(this.building)
                .name(this.name)
                .location(this.location)
                .time(this.time)
                .phone(this.phone)
                .build();
    }

    public FacilityDto(Facility facility) {
        this.building = facility.getBuilding();
        this.location = facility.getLocation();
        this.name = facility.getName();
        this.time = (facility.getTime() != null ? facility.getTime() : "");
        this.phone = (facility.getPhone() != null ? facility.getPhone() : "");

    }
}
