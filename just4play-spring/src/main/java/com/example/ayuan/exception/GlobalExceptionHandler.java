package com.example.ayuan.exception;

import com.example.ayuan.common.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局参数、异常拦截
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截表单参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public CommonResult bindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        return CommonResult.failed(ExceptionCodeEnum.VALIDATE_FAILED, Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
    }

    /**
     * 拦截JSON参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult bindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return CommonResult.failed(ExceptionCodeEnum.VALIDATE_FAILED,Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
    }

    /**
     * 拦截参数类型不正确
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult bindException(HttpMediaTypeNotSupportedException e){
        return CommonResult.failed(ExceptionCodeEnum.PRAM_NOT_MATCH,Objects.requireNonNull(e.getMessage()));
    }


    //声明要捕获的异常
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T> CommonResult<?> defaultExceptionHandler(Exception e) {
        e.printStackTrace();
        if(e instanceof ThreeException) {
            return CommonResult.failed(ExceptionCodeEnum.FAILED,Objects.requireNonNull(e.getMessage()));
        }
        if(e instanceof MissingServletRequestParameterException){
            return CommonResult.failed(ExceptionCodeEnum.PRAM_NOT_MATCH, Objects.requireNonNull(e.getMessage()));
        }
        //未知错误
        return CommonResult.failed(ExceptionCodeEnum.ERROR,e.getMessage());
    }
}
