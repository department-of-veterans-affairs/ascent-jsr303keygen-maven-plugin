package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;


public class CustomValidatorSequence implements DefaultGroupSequenceProvider<Object> {

	@Override
	public List getValidationGroups(Object object) {
		 final List<Class<?>> defaultGroupSequence = new ArrayList<Class<?>>();
		 return defaultGroupSequence;
	}
}
