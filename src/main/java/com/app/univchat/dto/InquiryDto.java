package com.app.univchat.dto;

import com.app.univchat.domain.Inquiry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;



@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Inquiry DTO")
public class InquiryDto {

    @ApiModelProperty(name = "문의 답변 수신 이메일", example = "catchat@catholic.ac.kr")
    public String receiverEmail;

    @ApiModelProperty(name = "문의 접수 내용", example = "실수로 계정을 탈퇴했는데, 삭제한 아이디를 복구할 수 있는 방법이 있을까요?")
    public String content;

    public Inquiry toEntity(String email, String inquiry) {

        return Inquiry.builder()
                .receiverEmail(email)
                .content(inquiry)
                .build();
    }
}
