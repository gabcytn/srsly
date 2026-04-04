package me.gabcytn.srsly.Helper;

import static me.gabcytn.srsly.DTO.Confidence.HIGH;
import static me.gabcytn.srsly.DTO.Confidence.LOW;
import static me.gabcytn.srsly.DTO.Difficulty.Easy;
import static me.gabcytn.srsly.DTO.Difficulty.Hard;

import java.math.BigDecimal;
import me.gabcytn.srsly.DTO.Confidence;
import me.gabcytn.srsly.DTO.Difficulty;
import me.gabcytn.srsly.DTO.ProblemStatus;

public class SpacedRepetitionHelper {
  private static final BigDecimal ZERO_POINT_TWO = BigDecimal.valueOf(0.2);
  private static final BigDecimal ZERO_POINT_ONE = BigDecimal.valueOf(0.1);
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
}
