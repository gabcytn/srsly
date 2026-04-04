package me.gabcytn.srsly.DTO.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import me.gabcytn.srsly.DTO.Annotation.Validator.InitialReviewValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = InitialReviewValidator.class)
public @interface ValidInitialReview {
  String message() default "Invalid conditional fields.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
