package com.transaction.exception;

import com.transaction.common.BaseResponse;
import com.transaction.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception e) {
        logger.error("System error: ", e);
        return BaseResponse.error(ResultCode.SYSTEM_ERROR);
    }
}
