package com.example.ruralfruits.common;

import com.example.ruralfruits.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(basePackages = "com.example.ruralfruits.controller")
public class GlobleExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobleExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exceptionHandler(Exception e){
        logger.error("系统异常",e);
        return Result.error(500,"系统异常");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result error(CustomException e){
        logger.error("自定义异常",e);
        return Result.error(500,e.getMessage());
    }
}
