package com.app.univchat.dto.school;

import com.app.univchat.domain.school.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "phone Dto")
public class PhoneDto {

    @ApiModelProperty(name = "학과", example = "국어국문학과")
    private String major;

    @ApiModelProperty(name = "전화번호", example = "02-2164-4201")
    private String phoneNumber;

    @ApiModelProperty(name = "이메일", example = "cukdkl@catholic.ac.kr")
    private String email;

    @ApiModelProperty(name = "위치", example = "M204")
    private String location;

    /**
     * Phone 엔티티 반환
     */
    public Phone toEntity(){
        return Phone.builder()
                .major(this.major)
                .phoneNumber((this.phoneNumber != null)?this.phoneNumber:"")
                .email((this.email != null)?this.email:"")
                .location((this.location != null)?this.location:"")
                .build();
    }
}
