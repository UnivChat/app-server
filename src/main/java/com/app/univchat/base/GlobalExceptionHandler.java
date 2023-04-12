package com.app.univchat.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BaseException 던질 시 실행
     */
    @ExceptionHandler({BaseException.class})
    private ResponseEntity handleBaseException(BaseException e) {
        return new ResponseEntity(new BaseResponse<>(e.getStatus().getCode(), e.getStatus().getMessage(), null), HttpStatus.valueOf(e.getStatus().getStatus()));
    }

}
