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
    user = new User();
    user.setId(UUID.randomUUID());
  }

  @Test
  public void shouldCreateInitialLimitWhenNotExists() {
    String key = generateKeyFromUser(user);
    when(repository.findById(key)).thenReturn(Optional.empty());

    service.incrementUsage(user);

    verify(repository, times(1)).save(any(AiCritiqueLimit.class));
  }

  @Test
  public void shouldIncrementUsageWhenLimitExists() {
    String key = generateKeyFromUser(user);
    AiCritiqueLimit limit = mock(AiCritiqueLimit.class);
    when(repository.findById(key)).thenReturn(Optional.of(limit));

    service.incrementUsage(user);

    verify(repository, times(1)).save(limit);
  }

  @Test
  public void shouldThrowExceptionWhenLimitKeyNotFound() {
    String key = generateKeyFromUser(user);
    when(repository.findById(key)).thenReturn(Optional.empty());
    assertThrows(GenericNotFoundException.class, () -> service.getResetTime(user));
  }

  @Test
  public void shouldReturnCorrectResetTime() {
    LocalDateTime dateTimeNow = LocalDateTime.now();
    AiCritiqueLimit limit = new AiCritiqueLimit("key", 1, dateTimeNow);

    String key = generateKeyFromUser(user);

    when(repository.findById(key)).thenReturn(Optional.of(limit));

    LocalDateTime expectedResult = dateTimeNow.plusHours(24);

    LocalDateTime result = service.getResetTime(user);

    assertEquals(expectedResult, result);
  }

  private String generateKeyFromUser(User user) {
    return String.format("user:%s", user.getId());
  }
}
