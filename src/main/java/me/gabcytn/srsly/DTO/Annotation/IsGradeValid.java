package me.gabcytn.srsly.DTO.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import me.gabcytn.srsly.DTO.Annotation.Validator.IsGradeValidAnnotationValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = IsGradeValidAnnotationValidator.class)
public @interface IsGradeValid {
	String message() default "Grade must be in the range of 0-5";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
