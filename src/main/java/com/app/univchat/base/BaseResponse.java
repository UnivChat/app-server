package com.app.univchat.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> {

    //API base응답

    @ApiModelProperty(value = "상태 코드", example = "1000")
    private String code;
    @ApiModelProperty(value = "결과 메시지", example = "요청에 성공하였습니다.")
    private  String message;
    private T result;


    public static <T> BaseResponse<T> ok(BaseResponseStatus baseResponseStatus) {
        return new BaseResponse<>(baseResponseStatus.getCode(), baseResponseStatus.getMessage(), null);
    }
    public static <T> BaseResponse<T> ok(BaseResponseStatus baseResponseStatus, T result) {
        return new BaseResponse<>(baseResponseStatus.getCode(), baseResponseStatus.getMessage(), result);
    }



}

