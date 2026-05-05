package me.gabcytn.srsly.AiCritique;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.UUID;
import me.gabcytn.srsly.AOP.AiLimiterAspect;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.AiException;
import me.gabcytn.srsly.Service.AiLimiterService;
import me.gabcytn.srsly.Service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AiLimiterAspectTest {
  @Mock AiLimiterService limiterService;
  @Mock UserService userService;

  @InjectMocks AiLimiterAspect aspect;

  @Test
  public void shouldProceedIfHasUsageLeft() throws Throwable {
    User user = mock(User.class);

    ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);

    when(userService.getCurrentUser()).thenReturn(user);
    when(limiterService.hasUsageLeft(user)).thenReturn(true);
    when(pjp.proceed()).thenReturn("success");

    Object result = aspect.checkAiUsageLimit(pjp);

    verify(limiterService, times(1)).incrementUsage(user);
    assertEquals("success", result);
  }

  @Test
  public void shouldThrowExceptionIfNoUsageLeft() {
    User user = mock(User.class);

    when(userService.getCurrentUser()).thenReturn(user);
    when(limiterService.hasUsageLeft(user)).thenReturn(Boolean.FALSE);

    assertThrows(
        AiException.class, () -> aspect.checkAiUsageLimit(mock(ProceedingJoinPoint.class)));

    verify(limiterService, times(1)).getResetTime(user);
  }
}
