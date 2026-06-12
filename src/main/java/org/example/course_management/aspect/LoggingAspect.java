package org.example.course_management.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.example.course_management.service..*(..)) || execution(* org.example.course_management.controller..*(..))")
    public Object profilePerformanceMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long markStart = System.currentTimeMillis();

        Object capturedPayload = joinPoint.proceed();

        long totalDeltaTime = System.currentTimeMillis() - markStart;
        logger.info("[ĐO LƯỜNG] Phần tử: {} thực thi trong -> {} ms",
                joinPoint.getSignature().toShortString(), totalDeltaTime);

        return capturedPayload;
    }
}
