package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class TestModel6 implements Serializable {
	
	private static final long serialVersionUID = 248506378568131588L;
	
	@NotEmpty
	@NotNull
	@Size(max = 35)
	private String stringValue1;

	public String getStringValue1() {
		return stringValue1;
	}

	public void setStringValue1(String stringValue1) {
		this.stringValue1 = stringValue1;
	}
	

}
