package com.transaction.aspect;

import com.transaction.annotation.ApiLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ApiLockAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLockAspect.class);
    private final StringRedisTemplate redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();

    public ApiLockAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.transaction.annotation.ApiLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiLock apiLock = method.getAnnotation(ApiLock.class);

        // 解析 lockName，支援 SpEL 表達式
        String lockValue = resolveLockValue(apiLock.lockName(), signature, joinPoint.getArgs());
        String key = apiLock.moduleName() + ":" + lockValue;

        try {
            // 嘗試加鎖
            boolean success = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", 1, TimeUnit.MINUTES));
            if (!success) {
                logger.warn("Failed to acquire lock: {}", key);
                throw new RuntimeException("Request is being processed. Please try again later.");
            }

            logger.info("Acquired Redis lock: {}", key);
            return joinPoint.proceed();

        } finally {
            // 解鎖
            redisTemplate.delete(key);
            logger.info("Released Redis lock: {}", key);
        }
    }

    private String resolveLockValue(String lockName, MethodSignature signature, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // 解析 SpEL 表達式
        return parser.parseExpression(lockName).getValue(context, String.class);
    }
}
