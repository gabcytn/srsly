package me.gabcytn.srsly.AOP;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

  @AfterThrowing(pointcut = "execution(* *..*.*(..))", throwing = "ex")
  public void logException(JoinPoint joinPoint, Exception exception) {
    log.error("Exception in method: {}", joinPoint.getSignature().getName());
    log.error("Message: {}", exception.getMessage());
  }
}
