package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CustomClassValidator1Impl implements ConstraintValidator<CustomClassValidator1, String> {

	@Override
	public void initialize(CustomClassValidator1 constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
		return false;
	}

}
