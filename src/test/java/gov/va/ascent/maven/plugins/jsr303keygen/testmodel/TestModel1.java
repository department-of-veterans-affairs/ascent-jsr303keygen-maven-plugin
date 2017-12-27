package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.group.GroupSequenceProvider;

@CustomClassValidator1
@GroupSequenceProvider(value = CustomValidatorSequence.class)
public class TestModel1 implements Serializable {
	
	private static final long serialVersionUID = -2301385565808951720L;
	
	public static final String EMPTY_MESSAGE = "Value cannot be empty.";
	public static final String NULL_MESSAGE = "Value cannot be null.";
	
	@NotEmpty(message = EMPTY_MESSAGE)
	@NotNull(message = NULL_MESSAGE)
	private String stringValue1;
	
	@NotEmpty
	@NotNull
	private String stringValue2;

	@CustomRandomField1Validator
	private String stringValue3;
	
	@Valid
	private TestModel2 testModel2_nullable;
	
	@Valid
	@NotNull
	private TestModel2 testModel2_notNullable;

	public String getStringValue1() {
		return stringValue1;
	}

	public void setStringValue1(String stringValue1) {
		this.stringValue1 = stringValue1;
	}

	public String getStringValue2() {
		return stringValue2;
	}

	public void setStringValue2(String stringValue2) {
		this.stringValue2 = stringValue2;
	}
	
	@NotEmpty
	@NotNull
	public String getStringValue3() {
		return stringValue3;
	}

	public void setStringValue3(String stringValue3) {
		this.stringValue3 = stringValue3;
	}
	
	public TestModel2 getTestModel2_nullable() {
		return testModel2_nullable;
	}

	public void setTestModel2_nullable(TestModel2 testModel2_nullable) {
		this.testModel2_nullable = testModel2_nullable;
	}

	public TestModel2 getTestModel2_notNullable() {
		return testModel2_notNullable;
	}

	public void setTestModel2_notNullable(TestModel2 testModel2_notNullable) {
		this.testModel2_notNullable = testModel2_notNullable;
	}

}
