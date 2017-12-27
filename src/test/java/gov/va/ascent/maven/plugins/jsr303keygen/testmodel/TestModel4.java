package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This model is used to test the @Valid existing on the getter to ensure we traverse in those cases.
 * @author jshrader
 */
public class TestModel4 implements Serializable {
	
	private static final long serialVersionUID = -46390317290881729L;
	
	@NotEmpty
	@NotNull
	private String stringValue1;
	
	private TestModel5 testModel5;

	public String getStringValue1() {
		return stringValue1;
	}

	public void setStringValue1(String stringValue1) {
		this.stringValue1 = stringValue1;
	}

	@Valid
	public TestModel5 getTestModel5() {
		return testModel5;
	}

	public void setTestModel5(TestModel5 testModel5) {
		this.testModel5 = testModel5;
	}
	

}
