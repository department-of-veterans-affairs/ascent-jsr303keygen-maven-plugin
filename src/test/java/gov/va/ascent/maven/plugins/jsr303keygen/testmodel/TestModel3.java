package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class TestModel3 implements Serializable {
	
	private static final long serialVersionUID = 6883325172986610550L;
	
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

}
