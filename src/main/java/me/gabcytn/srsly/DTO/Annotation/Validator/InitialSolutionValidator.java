package me.gabcytn.srsly.DTO.Annotation.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import me.gabcytn.srsly.DTO.Annotation.ValidInitialSolution;
import me.gabcytn.srsly.DTO.InitialSolutionDto;

public class InitialSolutionValidator implements ConstraintValidator<ValidInitialSolution, Object> {
  @Override
  public boolean isValid(Object o, ConstraintValidatorContext context) {
    if (!(o instanceof InitialSolutionDto dto) || dto.repetitions() == null) {
      return true;
    }

    if (dto.repetitions() == 0) {
      if (dto.confidence() == null && dto.lastReviewedAt() == null) {
        return true;
      }
      setAsInvalid(
          context,
          "Confidence and last reviewed at must be null if repetitions is 0.",
          "repetitions");
      return false;
    }

    if (dto.repetitions() < 0) {
      setAsInvalid(context, "Repetitions must be non-negative", "repetitions");
      return false;
    }

    if (dto.confidence() == null || dto.lastReviewedAt() == null) {
      setAsInvalid(
          context,
          "Confidence and last reviewed at are required in non-zero repetitions.",
          "confidence");
      return false;
    }

    if (dto.lastReviewedAt().isAfter(LocalDate.now())) {
      setAsInvalid(context, "Last reviewed at must not be after today's date.", "lastReviewedAt");
      return false;
    }
    return true;
  }

  private void setAsInvalid(
      ConstraintValidatorContext context, String message, String propertyNode) {
    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(message)
        .addPropertyNode(propertyNode)
        .addConstraintViolation();
  }
}
