package gov.va.ascent.maven.plugins.jsr303keygen.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.va.ascent.framework.transfer.AbstractTransferObject;

/**
 * Used to describe a operation we want error keys generated for.
 *
 * @author jshrader
 */
public final class OperationDescriptor extends AbstractTransferObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5587944399549307038L;

	/** The description. */
	private String name;

	/** The description. */
	private String description;

	/** The custom error keys. */
	private Map<String, String> errorKeys = new HashMap<>();

	/** The gen JSR 303 keys from classes. */
	private Set<String> genJsr303KeysFromClasses = new HashSet<>();

	/** The gen keys from interfaces. */
	private Set<String> genKeysFromInterfaces = new HashSet<>();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the error keys.
	 *
	 * @return the error keys
	 */
	public Map<String, String> getErrorKeys() {
		return errorKeys;
	}

	/**
	 * Sets the error keys.
	 *
	 * @param errorKeys the error keys
	 */
	public void setErrorKeys(final Map<String, String> errorKeys) {
		this.errorKeys = errorKeys;
	}

	/**
	 * Gets the gen jsr 303 keys from classes.
	 *
	 * @return the gen jsr 303 keys from classes
	 */
	public Set<String> getGenJsr303KeysFromClasses() {
		return genJsr303KeysFromClasses;
	}

	/**
	 * Sets the gen jsr 303 keys from classes.
	 *
	 * @param genJsr303KeysFromClasses the new gen jsr 303 keys from classes
	 */
	public void setGenJsr303KeysFromClasses(final Set<String> genJsr303KeysFromClasses) {
		this.genJsr303KeysFromClasses = genJsr303KeysFromClasses;
	}

	/**
	 * Gets the gen keys from interfaces.
	 *
	 * @return the gen keys from interfaces
	 */
	public Set<String> getGenKeysFromInterfaces() {
		return genKeysFromInterfaces;
	}

	/**
	 * Sets the gen keys from interfaces.
	 *
	 * @param genKeysFromInterfaces the new gen keys from interfaces
	 */
	public void setGenKeysFromInterfaces(final Set<String> genKeysFromInterfaces) {
		this.genKeysFromInterfaces = genKeysFromInterfaces;
	}

}
