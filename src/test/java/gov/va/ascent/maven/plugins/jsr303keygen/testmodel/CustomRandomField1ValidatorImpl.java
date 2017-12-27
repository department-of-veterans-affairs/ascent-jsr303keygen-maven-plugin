package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CustomRandomField1ValidatorImpl implements ConstraintValidator<CustomRandomField1Validator, String> {

	public void initialize(CustomRandomField1Validator constraintAnnotation) {
	}

	public boolean isValid(String value, ConstraintValidatorContext context) {
		return true;
	}
}
