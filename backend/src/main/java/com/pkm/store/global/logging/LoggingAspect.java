package com.pkm.store.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 모든 Controller의 메서드를 타겟으로 잡음
    @Pointcut("within(com.pkm.store.domain..controller..*)")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("[Request] {} {} | Method: {} | Args: {}", 
            request.getMethod(), request.getRequestURI(), 
            joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "controllerPointcut()", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        log.info("[Response Success] Method: {} | Return: {}", joinPoint.getSignature().getName(), result);
    }
}