package me.gabcytn.srsly.Helper;

import static me.gabcytn.srsly.DTO.Confidence.HIGH;
import static me.gabcytn.srsly.DTO.Confidence.LOW;
import static me.gabcytn.srsly.DTO.Difficulty.Easy;
import static me.gabcytn.srsly.DTO.Difficulty.Hard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import me.gabcytn.srsly.DTO.Confidence;
import me.gabcytn.srsly.DTO.Difficulty;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.Entity.SrsProblem;
import org.springframework.stereotype.Component;

@Component
public class SpacedRepetitionHelper {
  private static final BigDecimal ZERO_POINT_TWO = BigDecimal.valueOf(0.2);
  private static final BigDecimal ZERO_POINT_ONE = BigDecimal.valueOf(0.1);
  private static final BigDecimal ZERO_POINT_FOUR = BigDecimal.valueOf(0.4);
  private static final BigDecimal ONE_POINT_THREE = BigDecimal.valueOf(1.3);
  private static final BigDecimal ZERO_POINT_ZERO_EIGHT = BigDecimal.valueOf(0.08);
  private static final BigDecimal ZERO_POINT_ZERO_TWO = BigDecimal.valueOf(0.02);
  private static final BigDecimal FIVE = BigDecimal.valueOf(5);

  public int getInitialRepetitions(int repetitions) {
    validateNonNegativeRepetitions(repetitions);

    if (repetitions <= 2) {
      return repetitions;
    }

    if (repetitions == 3) {
      return 2;
    }

    return 3;
  }

  private void validateNonNegativeRepetitions(int n) {
    if (n < 0) {
      throw new RuntimeException("Repetitions must be a non-negative integer");
    }
  }

  public ProblemStatus getProblemStatus(int repetitions) {
    if (repetitions <= 2) {
      return ProblemStatus.LEARNING;
    }

    return ProblemStatus.REVIEWING;
  }

  public Double initialEaseFactor(Difficulty difficulty, Confidence confidence) {
    BigDecimal easeFactor = BigDecimal.valueOf(2.4);

    easeFactor = problemConfidenceAdjustment(confidence, easeFactor);
    easeFactor = problemDifficultyAdjustment(difficulty, easeFactor);

    return Math.min(easeFactor.doubleValue(), 2.6);
  }

  private BigDecimal problemConfidenceAdjustment(Confidence confidence, BigDecimal easeFactor) {
    if (confidence.equals(LOW)) {
      return easeFactor.subtract(ZERO_POINT_TWO);
    }

    if (confidence.equals(HIGH)) {
      return easeFactor.add(ZERO_POINT_TWO);
    }

    return easeFactor;
  }

  private BigDecimal problemDifficultyAdjustment(Difficulty difficulty, BigDecimal easeFactor) {
    if (difficulty.equals(Easy)) {
      return easeFactor.add(ZERO_POINT_ONE);
    }

    if (difficulty.equals(Hard)) {
      return easeFactor.subtract(ZERO_POINT_ONE);
    }

    return easeFactor;
  }

  public int initialInterval(int repetitions, double easeFactor) {
    validateNonNegativeRepetitions(repetitions);

    if (repetitions <= 1) {
      return 1;
    }

    if (repetitions == 2) {
      return 6;
    }

    return (int) Math.round(6 * Math.pow(easeFactor, repetitions - 2));
  }

  public SrsProblem reviewFailed(SrsProblem srsProblem, int grade) {
    BigDecimal easeFactor = BigDecimal.valueOf(srsProblem.getEaseFactor());
    BigDecimal failedEaseFactor = easeFactor.subtract(ZERO_POINT_TWO);

    LocalDate now = LocalDate.now();
    srsProblem.setEaseFactor(failedEaseFactor.max(ONE_POINT_THREE).doubleValue());
    srsProblem.setRepetitions(0);
    srsProblem.setInterval(1);
    srsProblem.setStatus(ProblemStatus.LEARNING);
    srsProblem.setLastAttemptAt(now);
    srsProblem.setNextAttemptAt(now.plusDays(1));

    return srsProblem;
  }

  public double calculateEaseFactor(SrsProblem srsProblem, int grade, LocalDate dateNow) {
    double previousEaseFactor = srsProblem.getEaseFactor();
    BigDecimal gradeBD = BigDecimal.valueOf(grade);
    BigDecimal gradeDiff = FIVE.subtract(gradeBD);

    BigDecimal inner = ZERO_POINT_ZERO_EIGHT.add(gradeDiff.multiply(ZERO_POINT_ZERO_TWO));

    BigDecimal adjustment = ZERO_POINT_ONE.subtract(gradeDiff.multiply(inner));

    BigDecimal result = BigDecimal.valueOf(previousEaseFactor).add(adjustment);

    return result.max(ONE_POINT_THREE).doubleValue()
        + calculateEaseFactorAdjustments(srsProblem, grade, dateNow);
  }

  private double calculateEaseFactorAdjustments(
      SrsProblem srsProblem, int grade, LocalDate dateNow) {
    if (dateNow.isAfter(srsProblem.getNextAttemptAt()) && grade == 5) {
      return 0.05;
    }

    return 0;
  }

  public long dateDifference(LocalDate from, LocalDate to) {
    return ChronoUnit.DAYS.between(from, to);
  }

  public int calculateSubsequentInterval(SrsProblem srsProblem, LocalDate dateNow) {
    int repetitions = srsProblem.getRepetitions();
    int interval = srsProblem.getInterval();
    double easeFactor = srsProblem.getEaseFactor();

    if (repetitions == 1) {
      return 1;
    }

    if (repetitions == 2) {
      return 6;
    }

    double timingMultiplier = getTimingMultiplier(srsProblem, dateNow);
    return (int) Math.round(interval * easeFactor * timingMultiplier);
  }

  private double getTimingMultiplier(SrsProblem problem, LocalDate dateNow) {
    BigDecimal timingMultiplier = BigDecimal.valueOf(1);
    if (dateNow.isAfter(problem.getNextAttemptAt())) {
      long delay = dateDifference(problem.getNextAttemptAt(), dateNow.plusDays(1));
      double ratio = (double) delay / problem.getInterval();
      timingMultiplier.add((ZERO_POINT_FOUR.multiply(BigDecimal.valueOf(ratio))));
    }
    return timingMultiplier.doubleValue();
  }

  public ProblemStatus determineProblemStatus(int interval, int repetitions) {
    if (interval >= 60 && repetitions >= 4) {
      return ProblemStatus.MASTERED;
    }

    if (repetitions > 2) {
      return ProblemStatus.REVIEWING;
    }

    return ProblemStatus.LEARNING;
  }
}
