package me.gabcytn.srsly.AOP;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.AiException;
import me.gabcytn.srsly.Service.AiLimiterService;
import me.gabcytn.srsly.Service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class AiLimiterAspect {
  private final UserService userService;
  private final AiLimiterService limiterService;

  @Around("execution(* me.gabcytn.srsly.Controller.AiController.index(..))")
  public Object checkAiUsageLimit(ProceedingJoinPoint pjp) throws Throwable {
    User user = userService.getCurrentUser();

    if (limiterService.hasUsageLeft(user)) {
      limiterService.incrementUsage(user);
      return pjp.proceed();
    }

    LocalDateTime nextReset = limiterService.getResetTime(user);
    throw new AiException(String.format("No AI critiques left. Token resets on %s.", nextReset));
  }
}
