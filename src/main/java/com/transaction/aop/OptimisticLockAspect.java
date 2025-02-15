package com.transaction.aop;

import jakarta.persistence.OptimisticLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class OptimisticLockAspect {

    private static final Logger logger = LoggerFactory.getLogger(OptimisticLockAspect.class);
    private static final int MAX_RETRIES = 3; // 最大重試次數

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object handleOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                return joinPoint.proceed();
            } catch (OptimisticLockException e) {
                attempts++;
                logger.warn("Optimistic Locking Failure. Retrying {}/{}", attempts, MAX_RETRIES);
                if (attempts >= MAX_RETRIES) {
                    throw new RuntimeException("Max retries reached. Operation failed.", e);
                }
            }
        }
        return null;
    }
}
