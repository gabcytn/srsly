package me.gabcytn.srsly.DTO.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.gabcytn.srsly.DTO.Annotation.IsEaseFactorRequiredCheck;
import me.gabcytn.srsly.DTO.SolutionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsEaseFactorRequiredCheckValidator
    implements ConstraintValidator<IsEaseFactorRequiredCheck, Object> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(IsEaseFactorRequiredCheckValidator.class);

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    if (!(obj instanceof SolutionDto dto)) {
      return true;
    }

    if (dto.getIsInitial() == null) {
      return true;
    }

    if (dto.getIsInitial() && dto.getGrade() == null) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Ease factor is required for initial solutions")
          .addPropertyNode("easeFactor")
          .addConstraintViolation();
      return false;
    }

    if (!dto.getIsInitial() && dto.getGrade() != null) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Ease factor must be null for non-initial solutions")
          .addPropertyNode("easeFactor")
          .addConstraintViolation();
      return false;
    }

    return true;
  }

}
