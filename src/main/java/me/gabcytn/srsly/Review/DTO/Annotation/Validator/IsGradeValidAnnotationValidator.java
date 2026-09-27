package me.gabcytn.srsly.Review.DTO.Annotation.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.gabcytn.srsly.Review.DTO.Annotation.IsGradeValid;

public class IsGradeValidAnnotationValidator implements ConstraintValidator<IsGradeValid, Integer> {
  @Override
  public boolean isValid(Integer grade, ConstraintValidatorContext constraintValidatorContext) {
    if (grade == null) {
      return true;
    }
    return grade >= 0 && grade <= 5;
  }
}
