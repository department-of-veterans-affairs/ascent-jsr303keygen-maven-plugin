package gov.va.ascent.maven.plugins.jsr303keygen.model;

import gov.va.ascent.framework.transfer.AbstractTransferObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.LinkedHashSet;


/**
 * The Class AscentJsr303KeyGenDescriptor is the primary descriptor input for the plugin. This defines what we want generated.
 *
 * @author jshrader
 */
//jshrader: permitting this in this model class as I want this order maintained in this generator based on insertion order
@SuppressWarnings("PMD.LooseCoupling" )
public final class AscentJsr303KeyGenDescriptor extends AbstractTransferObject{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4231208517816535744L;

	protected String[] getToStringEqualsHashExcludeFields() {
		return new String[0];
	}

	/** The OPTIONAL custom message bundles. */
	private LinkedHashSet<String> customMessageBundles;

	/** The operation descriptors. */
	private LinkedHashSet<OperationDescriptor> operationDescriptors;

	/**
	 * Gets the operation descriptors.
	 *
	 * @return the operation descriptors
	 */
	public LinkedHashSet<OperationDescriptor> getOperationDescriptors() {
		return operationDescriptors;
	}

	/**
	 * Sets the operation descriptors.
	 *
	 * @param operationDescriptors the new operation descriptors
	 */
	public void setOperationDescriptors(final LinkedHashSet<OperationDescriptor> operationDescriptors) {
		this.operationDescriptors = operationDescriptors;
	}

	/**
	 * Gets the custom message bundles.
	 *
	 * @return the custom message bundles
	 */
	public LinkedHashSet<String> getCustomMessageBundles() {
		return customMessageBundles;
	}

	/**
	 * Sets the custom message bundles.
	 *
	 * @param customMessageBundles the new custom message bundles
	 */
	public void setCustomMessageBundles(final LinkedHashSet<String> customMessageBundles) {
		this.customMessageBundles = customMessageBundles;
	}

	public String toString() {
		ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
		reflectionToStringBuilder.setExcludeFieldNames(this.getToStringEqualsHashExcludeFields());
		return reflectionToStringBuilder.toString();
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, this.getToStringEqualsHashExcludeFields());
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, this.getToStringEqualsHashExcludeFields());
	}

}
