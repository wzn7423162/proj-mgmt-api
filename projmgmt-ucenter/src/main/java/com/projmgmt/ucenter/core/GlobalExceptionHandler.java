package com.projmgmt.ucenter.core;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public AjaxResult handleAll(HttpServletRequest request, Exception ex) {
        // 打印完整堆栈，方便在 IntelliJ 控制台定位问题
        log.error("Unhandled exception - {} {}", request.getMethod(), request.getRequestURI(), ex);
        return AjaxResult.error("服务端异常，请联系管理员");
    }
}


