package me.gabcytn.srsly.AiCritique;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.AI.AiCritiqueLimit;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Repository.AiCritiqueLimitRepository;
import me.gabcytn.srsly.Service.AiLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AiLimiterServiceTest {
  @Mock private AiCritiqueLimitRepository repository;

  @InjectMocks private AiLimiterService service;

  private User user;

  @BeforeEach
  void setup() {
    user = mock(User.class);
  }

  @Test
  public void incrementUsage_ShouldCreateInitialLimitWhenNotExists() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());

    service.incrementUsage(user);

    verify(repository, times(1)).save(any(AiCritiqueLimit.class));
    verify(repository).save(argThat(limit -> limit.getUsageCount().equals(1)));
  }

  @Test
  public void incrementUsage_ShouldIncrementUsageWhenLimitExists() {
    AiCritiqueLimit limit = AiCritiqueLimit.ofInitial(UUID.randomUUID().toString());

    when(repository.findById(anyString())).thenReturn(Optional.of(limit));

    service.incrementUsage(user);

    verify(repository, times(1)).save(limit);
    verify(repository).save(argThat(arg -> arg.getUsageCount().equals(2)));
  }

  @Test
  public void getResetTime_ShouldThrowExceptionWhenLimitKeyNotFound() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(GenericNotFoundException.class, () -> service.getResetTime(user));
  }

  @Test
  public void getResetTime_ShouldReturnCorrectResetTime() {
    LocalDateTime dateTimeNow = LocalDateTime.now();
    AiCritiqueLimit limit = new AiCritiqueLimit(UUID.randomUUID().toString(), 1, dateTimeNow);

    when(repository.findById(anyString())).thenReturn(Optional.of(limit));

    LocalDateTime expectedResult = dateTimeNow.plusHours(24);
    LocalDateTime result = service.getResetTime(user);

    assertEquals(expectedResult, result);
    assertEquals(dateTimeNow.plusDays(1), result);
  }
}
