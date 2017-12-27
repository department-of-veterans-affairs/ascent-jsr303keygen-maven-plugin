package gov.va.ascent.maven.plugins.jsr303keygen.testmodel;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This model has regex, size and some more advanced annotations to test that aspect as well as a collection
 * @author jshrader
 */
public class TestModel5 implements Serializable {
	
	private static final long serialVersionUID = 248506378568131588L;
	
	@NotEmpty
	@NotNull
	@Size(max = 35)
    @Pattern(regexp = "^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$")
	private String stringValue1;
	
	@Size(min = 0, max = 100)
    @Valid
    protected List<TestModel6> testModel6Collection;
	
	@Size(min = 0, max = 100)
    @Valid
    protected TestModel7[] testModel7Array;
	
    protected List<TestModel6> testModel6Collection_ValidOnGetter;
	
    protected TestModel7[] testModel7Array_ValidOnGetter;

	public String getStringValue1() {
		return stringValue1;
	}

	public void setStringValue1(String stringValue1) {
		this.stringValue1 = stringValue1;
	}
	
	public List<TestModel6> getTestModel6Collection() {
		return testModel6Collection;
	}

	public void setTestModel6Collection(List<TestModel6> testModel6Collection) {
		this.testModel6Collection = testModel6Collection;
	}

	public TestModel7[] getTestModel7Array() {
		return testModel7Array;
	}

	public void setTestModel7Array(TestModel7[] testModel7Array) {
		this.testModel7Array = testModel7Array;
	}

	@Size(min = 0, max = 100)
    @Valid
	public List<TestModel6> getTestModel6Collection_ValidOnGetter() {
		return testModel6Collection_ValidOnGetter;
	}

	public void setTestModel6Collection_ValidOnGetter(List<TestModel6> testModel6Collection_ValidOnGetter) {
		this.testModel6Collection_ValidOnGetter = testModel6Collection_ValidOnGetter;
	}

	@Size(min = 0, max = 100)
    @Valid
	public TestModel7[] getTestModel7Array_ValidOnGetter() {
		return testModel7Array_ValidOnGetter;
	}

	public void setTestModel7Array_ValidOnGetter(TestModel7[] testModel7Array_ValidOnGetter) {
		this.testModel7Array_ValidOnGetter = testModel7Array_ValidOnGetter;
	}
	
	
}
