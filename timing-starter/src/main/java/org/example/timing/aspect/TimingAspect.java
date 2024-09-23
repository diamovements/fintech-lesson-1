package org.example.timing.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@Slf4j
public class TimingAspect {
    private static final Logger logger = LoggerFactory.getLogger(TimingAspect.class);

    @Around("@annotation(org.example.timing.Timing)")
    public Object measureTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        Class<?> className = proceedingJoinPoint.getTarget().getClass();
        logger.info("\u001B[32m" + "Method [{}] from [{}] has started...", methodName, className);
        Instant startTime = Instant.now();
        Object result = proceedingJoinPoint.proceed();
        Instant endTime = Instant.now();
        logger.info("\u001B[32m" + "Method [{}] from [{}] has finished in [{}] nanos.", methodName, className, Duration.between(startTime, endTime).getNano());
        return result;
    }
}
