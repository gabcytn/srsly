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
import me.gabcytn.srsly.Entity.SolvedProblem;
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

  public SolvedProblem reviewFailed(SolvedProblem solvedProblem, int grade) {
    BigDecimal easeFactor = BigDecimal.valueOf(solvedProblem.getEaseFactor());
    BigDecimal failedEaseFactor = easeFactor.subtract(ZERO_POINT_TWO);

    LocalDate now = LocalDate.now();
    solvedProblem.setEaseFactor(failedEaseFactor.max(ONE_POINT_THREE).doubleValue());
    solvedProblem.setRepetitions(0);
    solvedProblem.setInterval(1);
    solvedProblem.setStatus(ProblemStatus.LEARNING);
    solvedProblem.setLastAttemptAt(now);
    solvedProblem.setNextAttemptAt(now.plusDays(1));

    return solvedProblem;
  }

  public double calculateEaseFactor(SolvedProblem solvedProblem, int grade, LocalDate dateNow) {
    double previousEaseFactor = solvedProblem.getEaseFactor();
    BigDecimal gradeBD = BigDecimal.valueOf(grade);
    BigDecimal gradeDiff = FIVE.subtract(gradeBD);

    BigDecimal inner = ZERO_POINT_ZERO_EIGHT.add(gradeDiff.multiply(ZERO_POINT_ZERO_TWO));

    BigDecimal adjustment = ZERO_POINT_ONE.subtract(gradeDiff.multiply(inner));

    BigDecimal result = BigDecimal.valueOf(previousEaseFactor).add(adjustment);

    return result.max(ONE_POINT_THREE).doubleValue()
        + calculateEaseFactorAdjustments(solvedProblem, grade, dateNow);
  }

  private double calculateEaseFactorAdjustments(
			SolvedProblem solvedProblem, int grade, LocalDate dateNow) {
    if (dateNow.isAfter(solvedProblem.getNextAttemptAt()) && grade == 5) {
      return 0.05;
    }

    return 0;
  }

  public long dateDifference(LocalDate from, LocalDate to) {
    return ChronoUnit.DAYS.between(from, to);
  }

  public int calculateSubsequentInterval(SolvedProblem solvedProblem, LocalDate dateNow) {
    int repetitions = solvedProblem.getRepetitions();
    int interval = solvedProblem.getInterval();
    double easeFactor = solvedProblem.getEaseFactor();

    if (repetitions == 1) {
      return 1;
    }

    if (repetitions == 2) {
      return 6;
    }

    double timingMultiplier = getTimingMultiplier(solvedProblem, dateNow);
    return (int) Math.round(interval * easeFactor * timingMultiplier);
  }

  private double getTimingMultiplier(SolvedProblem problem, LocalDate dateNow) {
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
