package me.gabcytn.srsly.DTO.Annotation.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.gabcytn.srsly.Controller.ReviewedProblem;
import me.gabcytn.srsly.DTO.Annotation.IsGradeValid;

public class IsGradeValidAnnotationValidator implements ConstraintValidator<IsGradeValid, Object> {
  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    if (!(o instanceof ReviewedProblem(int grade))) return false;

    return grade >= 0 && grade <= 5;
  }
}
