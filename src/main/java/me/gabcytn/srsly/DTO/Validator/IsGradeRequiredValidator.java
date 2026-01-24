package me.gabcytn.srsly.DTO.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.gabcytn.srsly.DTO.Annotation.IsGradeRequired;
import me.gabcytn.srsly.DTO.SolutionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsGradeRequiredValidator
    implements ConstraintValidator<IsGradeRequired, Object> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(IsGradeRequiredValidator.class);

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
              "Grade is required for initial solutions")
          .addPropertyNode("grade")
          .addConstraintViolation();
      return false;
    }

    if (!dto.getIsInitial() && dto.getGrade() != null) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Grade must be null for non-initial solutions")
          .addPropertyNode("grade")
          .addConstraintViolation();
      return false;
    }

    return true;
  }

}
