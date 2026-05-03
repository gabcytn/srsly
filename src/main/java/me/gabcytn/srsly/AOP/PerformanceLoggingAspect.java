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

  @Around("execution(* me.gabcytn.srsly.Service.AiService.critique(..))")
  public Object logAiCritiquePerformance(ProceedingJoinPoint pjp) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object res = pjp.proceed();
    long endTime = System.currentTimeMillis();

    log.info("Time taken for AI Critique: {} ms", endTime - startTime);

    return res;
  }
}
