package me.gabcytn.srsly.DTO.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import me.gabcytn.srsly.DTO.Validator.IsEaseFactorRequiredCheckValidator;

@Documented
@Constraint(validatedBy = IsEaseFactorRequiredCheckValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsEaseFactorRequiredCheck {
  String message() default "Mismatch of isInitial field and easeFactor";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
