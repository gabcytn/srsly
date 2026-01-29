package me.gabcytn.srsly.DTO.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import me.gabcytn.srsly.DTO.Annotation.Validator.InitialSolutionValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = InitialSolutionValidator.class)
public @interface ValidInitialSolution {
	String message() default "Invalid conditional fields.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
