package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Constraint(validatedBy = { CustomRandomField1ValidatorImpl.class })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface CustomClassValidator1 {
	/**
	 * The Message.
	 */
	String message() default "{gov.va.ascent.maven.plugins.jaxb.swagger.testmodel.validation.CustomClassValidator1.message}";

	/**
	 * Groups.
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload.
	 */
	Class<? extends Payload>[] payload() default {};
}
