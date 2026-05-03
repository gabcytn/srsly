package me.gabcytn.srsly.Helper;

import static me.gabcytn.srsly.DTO.Confidence.HIGH;
import static me.gabcytn.srsly.DTO.Confidence.LOW;
import static me.gabcytn.srsly.DTO.Difficulty.EASY;
import static me.gabcytn.srsly.DTO.Difficulty.HARD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import me.gabcytn.srsly.DTO.Confidence;
import me.gabcytn.srsly.DTO.Difficulty;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.Entity.ReviewProblem;
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

  public static int normalizeInitialReps(int repetitions) {
    validateNonNegativeRepetitions(repetitions);

    if (repetitions <= 2) {
      return repetitions;
    }

    if (repetitions == 3) {
      return 2;
    }

    return 3;
  }

  private static void validateNonNegativeRepetitions(int n) {
    if (n < 0) {
      throw new RuntimeException("Repetitions must be a non-negative integer");
    }
  }

  public static ProblemStatus getProblemStatus(int repetitions) {
    if (repetitions <= 2) {
      return ProblemStatus.LEARNING;
    }

    return ProblemStatus.REVIEWING;
  }

  public static Double initialEaseFactor(Difficulty difficulty, Confidence confidence) {
    BigDecimal easeFactor = BigDecimal.valueOf(2.4);

    easeFactor = problemConfidenceAdjustment(confidence, easeFactor);
    easeFactor = problemDifficultyAdjustment(difficulty, easeFactor);

    return Math.min(easeFactor.doubleValue(), 2.6);
  }

  private static BigDecimal problemConfidenceAdjustment(Confidence confidence, BigDecimal easeFactor) {
    if (confidence.equals(LOW)) {
      return easeFactor.subtract(ZERO_POINT_TWO);
    }

    if (confidence.equals(HIGH)) {
      return easeFactor.add(ZERO_POINT_TWO);
    }

    return easeFactor;
  }

  private static BigDecimal problemDifficultyAdjustment(Difficulty difficulty, BigDecimal easeFactor) {
    if (difficulty.equals(EASY)) {
      return easeFactor.add(ZERO_POINT_ONE);
    }

    if (difficulty.equals(HARD)) {
      return easeFactor.subtract(ZERO_POINT_ONE);
    }

    return easeFactor;
  }

  public static int initialInterval(int repetitions, double easeFactor) {
    validateNonNegativeRepetitions(repetitions);

    if (repetitions <= 1) {
      return 1;
    }

    if (repetitions == 2) {
      return 6;
    }

    return (int) Math.round(6 * Math.pow(easeFactor, repetitions - 2));
  }

  public static ReviewProblem reviewFailed(ReviewProblem reviewProblem, int grade) {
    BigDecimal easeFactor = BigDecimal.valueOf(reviewProblem.getEaseFactor());
    BigDecimal failedEaseFactor = easeFactor.subtract(ZERO_POINT_TWO);

    LocalDate now = LocalDate.now();
    reviewProblem.setEaseFactor(failedEaseFactor.max(ONE_POINT_THREE).doubleValue());
    reviewProblem.setRepetitions(0);
    reviewProblem.setInterval(1);
    reviewProblem.setStatus(ProblemStatus.LEARNING);
    reviewProblem.setLastAttemptAt(now);
    reviewProblem.setNextAttemptAt(now.plusDays(1));

    return reviewProblem;
  }

  public static double calculateEaseFactor(ReviewProblem reviewProblem, int grade, LocalDate dateNow) {
    double previousEaseFactor = reviewProblem.getEaseFactor();
    BigDecimal gradeBD = BigDecimal.valueOf(grade);
    BigDecimal gradeDiff = FIVE.subtract(gradeBD);

    BigDecimal inner = ZERO_POINT_ZERO_EIGHT.add(gradeDiff.multiply(ZERO_POINT_ZERO_TWO));

    BigDecimal adjustment = ZERO_POINT_ONE.subtract(gradeDiff.multiply(inner));

    BigDecimal result = BigDecimal.valueOf(previousEaseFactor).add(adjustment);

    return result.max(ONE_POINT_THREE).doubleValue()
        + calculateEaseFactorAdjustments(reviewProblem, grade, dateNow);
  }

  private static double calculateEaseFactorAdjustments(
			ReviewProblem reviewProblem, int grade, LocalDate dateNow) {
    if (dateNow.isAfter(reviewProblem.getNextAttemptAt()) && grade == 5) {
      return 0.05;
    }

    return 0;
  }

  public static long dateDifference(LocalDate from, LocalDate to) {
    return ChronoUnit.DAYS.between(from, to);
  }

  public static int calculateSubsequentInterval(ReviewProblem reviewProblem, LocalDate dateNow) {
    int repetitions = reviewProblem.getRepetitions();
    int interval = reviewProblem.getInterval();
    double easeFactor = reviewProblem.getEaseFactor();

    if (repetitions == 1) {
      return 1;
    }

    if (repetitions == 2) {
      return 6;
    }

    double timingMultiplier = getTimingMultiplier(reviewProblem, dateNow);
    return (int) Math.round(interval * easeFactor * timingMultiplier);
  }

  private static double getTimingMultiplier(ReviewProblem problem, LocalDate dateNow) {
    BigDecimal timingMultiplier = BigDecimal.valueOf(1);
    if (dateNow.isAfter(problem.getNextAttemptAt())) {
      long delay = dateDifference(problem.getNextAttemptAt(), dateNow.plusDays(1));
      double ratio = (double) delay / problem.getInterval();
      timingMultiplier.add((ZERO_POINT_FOUR.multiply(BigDecimal.valueOf(ratio))));
    }
    return timingMultiplier.doubleValue();
  }

  public static ProblemStatus determineProblemStatus(int interval, int repetitions) {
    if (interval >= 60 && repetitions >= 4) {
      return ProblemStatus.MASTERED;
    }

    if (repetitions > 2) {
      return ProblemStatus.REVIEWING;
    }

    return ProblemStatus.LEARNING;
  }
}
