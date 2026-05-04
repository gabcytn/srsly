package me.gabcytn.srsly.AOP;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

  @Around("@annotation(me.gabcytn.srsly.AOP.Annotation.LogPerformance)")
  public Object logAiCritiquePerformance(ProceedingJoinPoint pjp) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object res = pjp.proceed();
    long endTime = System.currentTimeMillis();

    log.info("Time taken for {}: {} ms", pjp.getSignature().getName(), endTime - startTime);

    return res;
  }
}
