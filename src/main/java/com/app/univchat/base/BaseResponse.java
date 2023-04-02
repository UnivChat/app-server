package com.app.univchat.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"httpStatus", "code", "message", "result"})
public class BaseResponse<T> {

    //API base응답
    private HttpStatus httpStatus;
    private String code;
    private  String message;
    private T result;



    public static <T> BaseResponse<T> ok(HttpStatus httpStatus, BaseResponseStatus baseResponseStatus, T result) {
        return new BaseResponse<>(httpStatus, baseResponseStatus.getCode(), baseResponseStatus.getMessage(), result);
    }

    public static <T> BaseResponse<T> error(HttpStatus httpStatus, BaseResponseStatus baseResponseStatus) {
        return new BaseResponse<>(httpStatus, baseResponseStatus.getCode(), baseResponseStatus.getMessage(), null);
    }


}

