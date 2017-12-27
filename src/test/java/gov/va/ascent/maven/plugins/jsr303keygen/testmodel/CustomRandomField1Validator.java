package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { CustomRandomField1ValidatorImpl.class })
public @interface CustomRandomField1Validator {
	/**
	 * The Message.
	 */
	String message() default "{gov.va.ascent.maven.plugins.jaxb.swagger.testmodel.validation.CustomRandomField1Validator.message}";

	/**
	 * Groups.
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload.
	 */
	Class<? extends Payload>[] payload() default {};
}
