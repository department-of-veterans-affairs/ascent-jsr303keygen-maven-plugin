package gov.va.ascent.maven.plugins.jsr303keygen;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.validation.ModelValidator;

/**
 * The Class ErrorIntrospector contains the methods for introspecting classes for errors. This includes introspecting a graph for JSR
 * 303 errors and also introspecting interfaces, possibly other techniques for looking at class(es) to fetch out errors.
 *
 * @author jshrader
 */
public final class ErrorIntrospector {

	/** The Constant LOGGER. */
	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(ErrorIntrospector.class);

	/**
	 * Utility class hidden constructor
	 */
	protected ErrorIntrospector() {
	}

	protected static void introspectInterfaceForKeys(final MessageInterpolator messageInterpolator, final Class<?> clazz,
			final Map<String, String> jsr303Errors) throws MojoExecutionException {
		Assert.notNull(clazz, "clazz to introspect for interface keys cannot be null!");
		for (final Field field : FieldUtils.getAllFields(clazz)) {
			String messageKey = null;
			try {
				messageKey = (String) field.get(null);
			} catch (final IllegalArgumentException illegalArg) {
				invalidFieldValue(illegalArg);
			} catch (final IllegalAccessException illegalAccess) {
				invalidFieldValue(illegalAccess);
			}
			String message = messageInterpolator.noInterpolate(messageKey);
			if (messageKey == null || messageKey.equals(message)) {
				message = "";
			}
			jsr303Errors.put(messageKey, message);
		}
	}

	/**
	 * logs the error and throws runtime exception
	 *
	 * @param exception exception
	 */
	private static void invalidFieldValue(final Exception exception) throws MojoExecutionException {
		final MojoExecutionException mojoExecutionException =
				new MojoExecutionException("Verify your messages in the Interface.", exception);
		LOGGER.error(mojoExecutionException.getMessage(), mojoExecutionException);
		throw mojoExecutionException;
	}

	/**
	 * Instrospect class for JSR 303s.
	 *
	 * @param clazz the clazz
	 * @param nodepath the nodepath
	 * @param jsr303Errors the jsr303Errors
	 */
	protected static void instrospectClassForJSR303s(final MessageInterpolator messageInterpolator, final Class<?> clazz,
			final String nodepath, final Map<String, String> jsr303Errors) {
		Assert.notNull(clazz, "clazz to introspect for JSR303s cannot be null!");
		Assert.notNull(nodepath, "nodepath to introspect for JSR303s cannot be null!");
		Assert.notNull(jsr303Errors, "jsr303Errors map to use during introspect for JSR303s cannot be null!");

		for (final Annotation clsAnnotation : clazz.getDeclaredAnnotations()) {
			introspectAnnotationForJSR303(messageInterpolator, clsAnnotation, nodepath, jsr303Errors);
		}

		instrospectClassFieldsForJSR303s(messageInterpolator, clazz, nodepath, jsr303Errors);
		instrospectClassPropertiesForJSR303s(messageInterpolator, clazz, nodepath, jsr303Errors);
	}

	/**
	 * Instrospect the class fields for JSR 303s.
	 *
	 * @param clazz the clazz
	 * @param nodepath the nodepath
	 * @param jsr303Errors the jsr303Errors
	 */
	private static void instrospectClassFieldsForJSR303s(final MessageInterpolator messageInterpolator, final Class<?> clazz,
			final String nodepath, final Map<String, String> jsr303Errors) {
		for (final Field field : FieldUtils.getAllFields(clazz)) {
			for (final Annotation fldAnnotation : field.getDeclaredAnnotations()) {
				introspectAnnotationForJSR303(messageInterpolator, fldAnnotation, growNodepath(nodepath, field.getName()),
						jsr303Errors);
				recurseDeeperAsNeeded(messageInterpolator, nodepath, jsr303Errors, field, fldAnnotation);
			}
		}
	}

	/**
	 * Instrospect the class properties for JSR 303s.
	 *
	 * @param clazz the clazz
	 * @param nodepath the nodepath
	 * @param jsr303Errors the jsr303Errors
	 */
	private static void instrospectClassPropertiesForJSR303s(final MessageInterpolator messageInterpolator, final Class<?> clazz,
			final String nodepath, final Map<String, String> jsr303Errors) {
		for (final PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
			if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null
					&& propertyDescriptor.getReadMethod().getAnnotations() != null) {
				for (final Annotation propAnnotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					introspectAnnotationForJSR303(messageInterpolator, propAnnotation,
							growNodepath(nodepath, propertyDescriptor.getName()), jsr303Errors);
					recurseDeeperAsNeeded(messageInterpolator, nodepath, jsr303Errors,
							FieldUtils.getField(clazz, propertyDescriptor.getName(), true), propAnnotation);
				}
			}
		}
	}

	/**
	 * Recurse deeper as needed. Looks at an annotation to see if it's of the type Valid to trigger us to continue deeper into the
	 * recursive rabit hole.
	 *
	 * @param nodepath the nodepath
	 * @param jsr303Errors the jsr 303 errors
	 * @param field the field
	 * @param fldAnnotation the fld annotation
	 */
	private static void recurseDeeperAsNeeded(final MessageInterpolator messageInterpolator, final String nodepath,
			final Map<String, String> jsr303Errors, final Field field, final Annotation fldAnnotation) {
		if (fldAnnotation instanceof Valid) {
			if (Collection.class.isAssignableFrom(field.getType())) {
				final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				final Class<?> collectionClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
				instrospectClassForJSR303s(messageInterpolator, collectionClazz, growNodepath(nodepath, field.getName(), true),
						jsr303Errors);
			} else if (field.getType().isArray()) {
				instrospectClassForJSR303s(messageInterpolator, field.getType().getComponentType(),
						growNodepath(nodepath, field.getName(), true), jsr303Errors);
			}
			instrospectClassForJSR303s(messageInterpolator, field.getType(), growNodepath(nodepath, field.getName()), jsr303Errors);
		}
	}

	/**
	 * Introspect annotation for JSR 303.
	 *
	 * @param annotation the annotation
	 * @param nodepath the nodepath
	 * @param jsr303Errors the jsr303Errors
	 */
	private static void introspectAnnotationForJSR303(final MessageInterpolator messageInterpolator, final Annotation annotation,
			final String nodepath, final Map<String, String> jsr303Errors) {
		if (isJSR303Annotation(annotation)) {
			final String messageKey = (String) AnnotationUtils.getAnnotationAttributes(annotation).get("message");
			String message = messageInterpolator.interpolate(annotation);
			if (messageKey.equals(message)) {
				message = "";
			}
			jsr303Errors.put(ModelValidator.convertKeyToNodepathStyle(nodepath, messageKey), message);
		}
	}

	/**
	 * Grow nodepath.
	 *
	 * @param nodepath the nodepath
	 * @param newNodeName the new node name
	 * @return the string
	 */
	private static String growNodepath(final String nodepath, final String newNodeName) {
		return growNodepath(nodepath, newNodeName, false);
	}

	/**
	 * Grow nodepath.
	 *
	 * @param nodepath the nodepath
	 * @param newNodeName the new node name
	 * @param isCollectionIndicator the is collection indicator
	 * @return the string
	 */
	private static String growNodepath(final String nodepath, final String newNodeName, final boolean isCollectionIndicator) {
		Assert.notNull(nodepath, "nodepath to grow cannot be null!");
		Assert.notNull(newNodeName, "newNodeName grow cannot be null!");

		String returnIt;

		if (StringUtils.isEmpty(nodepath)) {
			returnIt = newNodeName;
		} else {
			returnIt = nodepath + "." + newNodeName;
		}

		if (isCollectionIndicator) {
			returnIt = returnIt + "[#]";
		}

		return returnIt;
	}

	/**
	 * Checks if is JSR 303 annotation.
	 *
	 * This alogorithm may not be the best way, but it's the only way I know of right now to with reasonable accuracy determine if an
	 * annotation is a JSR 303 one.
	 *
	 * @param annotation the annotation
	 * @return true, if is JSR 303 annotation
	 */
	private static boolean isJSR303Annotation(final Annotation annotation) {
		final Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
		return attributes != null && attributes.containsKey("message") && attributes.containsKey("groups")
				&& attributes.containsKey("payload");
	}

}
