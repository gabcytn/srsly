package me.gabcytn.srsly.Review.DTO.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import me.gabcytn.srsly.Review.DTO.Annotation.Validator.IsGradeValidAnnotationValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = IsGradeValidAnnotationValidator.class)
public @interface IsGradeValid {
  String message() default "Grade must be in the range of 0-5";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
