package gov.va.ascent.maven.plugins.jsr303keygen;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;


/**
 * The Class MessageInterpolator is a customized version of
 * org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator that lets us grab messages out of the standard
 * resource bundles using the same techniques used by HibernateValidator for rendering into our output of this plugin.
 *
 * @author jshrader
 */
public final class MessageInterpolator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageInterpolator.class);
	
	/**
	 * The name of the default message bundle.
	 */
	public static final String DEFAULT_VALIDATION_MESSAGES = "org.hibernate.validator.ValidationMessages";

	/**
	 * The name of the user-provided message bundle as defined in the specification.
	 */
	public static final String DEFAULT_USER_VALIDATION_MESSAGES = "ValidationMessages";

	/**
	 * Regular expression used to do message interpolation.
	 */
	private static final Pattern MESSAGE_PARAMETER_PATTERN = Pattern.compile("(\\{[^\\}]+?\\})");

	/**
	 * The default locale in the current JVM.
	 */
	private final Locale defaultLocale;

	/**
	 * Loads user-specified resource bundles.
	 */
	private final Set<ResourceBundleLocator> userResourceBundleLocators = new LinkedHashSet<ResourceBundleLocator>();

	/**
	 * Loads built-in resource bundles.
	 */
	private final ResourceBundleLocator defaultResourceBundleLocator;

	/**
	 * Step 1-3 of message interpolation can be cached. We do this in this map.
	 */
	private final ConcurrentMap<LocalisedMessage, String> resolvedMessages = new ConcurrentHashMap<LocalisedMessage, String>();

	/**
	 * Flag indicating whether this interpolator should cache some of the interpolation steps.
	 */
	private final boolean cacheMessages;

	/**
	 * Instantiates a new message interpolator.
	 */
	public MessageInterpolator() {
		this(new PlatformResourceBundleLocator(DEFAULT_USER_VALIDATION_MESSAGES));
	}

	/**
	 * Instantiates a new message interpolator.
	 *
	 * @param userResourceBundleLocator the user resource bundle locator
	 */
	public MessageInterpolator(final ResourceBundleLocator userResourceBundleLocator) {
		this(userResourceBundleLocator, true);
	}

	/**
	 * Instantiates a new message interpolator.
	 *
	 * @param userResourceBundleLocator the user resource bundle locator
	 * @param cacheMessages the cache messages
	 */
	public MessageInterpolator(final ResourceBundleLocator userResourceBundleLocator, final boolean cacheMessages) {

		defaultLocale = Locale.getDefault();

		userResourceBundleLocators.add(userResourceBundleLocator);

		defaultResourceBundleLocator = new PlatformResourceBundleLocator(DEFAULT_VALIDATION_MESSAGES);
		this.cacheMessages = cacheMessages;
	}

	/**
	 * Instantiates a new message interpolator.
	 *
	 * @param customMessageSources the custom message sources
	 */
	public MessageInterpolator(final Set<String> customMessageSources) {
		this(customMessageSources, true);
	}

	/**
	 * Instantiates a new message interpolator.
	 *
	 * @param customMessageSources the custom message sources
	 * @param cacheMessages the cache messages
	 */
	public MessageInterpolator(final Set<String> customMessageSources, final boolean cacheMessages) {

		defaultLocale = Locale.getDefault();

		for (final String customMessageSource : customMessageSources) {
			userResourceBundleLocators.add(new PlatformResourceBundleLocator(customMessageSource));
		}

		defaultResourceBundleLocator = new PlatformResourceBundleLocator(DEFAULT_VALIDATION_MESSAGES);
		this.cacheMessages = cacheMessages;
	}

	/**
	 * Retrieve message for a annotation with interpolation.
	 *
	 * @param annotation the annotation
	 * @return the string
	 */
	public String interpolate(final Annotation annotation) {
		// probably no need for caching, but it could be done by parameters since the map
		// is immutable and uniquely built per Validation definition, the comparison has to be based on == and not equals though
		return interpolateMessage((String) AnnotationUtils.getAnnotationAttributes(annotation).get("message"),
				AnnotationUtils.getAnnotationAttributes(annotation), defaultLocale);
	}

	/**
	 * Retrieve message for a annotation with interpolation.
	 *
	 * @param annotation the annotation
	 * @param locale the locale
	 * @return the string
	 */
	public String interpolate(final Annotation annotation, final Locale locale) {
		return interpolateMessage((String) AnnotationUtils.getAnnotationAttributes(annotation).get("message"),
				AnnotationUtils.getAnnotationAttributes(annotation), locale);
	}

	/**
	 * Retrieve message for a messageKey without interpolation.
	 *
	 * @param messageKey the message key
	 * @return the string
	 */
	public String noInterpolate(final String messageKey) {
		return noInterpolate(messageKey, defaultLocale);
	}

	/**
	 * Retrieve message for a messageKey without interpolation.
	 *
	 * @param messageKey the message key
	 * @param locale the locale
	 * @return the string
	 */
	public String noInterpolate(final String messageKey, final Locale locale) {
		final LocalisedMessage localisedMessage = new LocalisedMessage(messageKey, locale);
		String resolvedMessage = null;

		if (cacheMessages) {
			resolvedMessage = resolvedMessages.get(localisedMessage);
		}

		// if the message is not already do the message resolution
		if (resolvedMessage == null) {
			for (final ResourceBundleLocator userResourceBundleLocator : userResourceBundleLocators) {
				final ResourceBundle userResourceBundle = userResourceBundleLocator.getResourceBundle(locale);
				try {
					resolvedMessage = userResourceBundle.getString(messageKey);
				} catch (final MissingResourceException mre) {
					LOGGER.debug("messageKey not found in bundle", mre);	
				}
				if (resolvedMessage != null) {
					break;
				}
			}

			if (resolvedMessage == null) {
				try {
					final ResourceBundle defaultResourceBundle = defaultResourceBundleLocator.getResourceBundle(locale);
					resolvedMessage = defaultResourceBundle.getString(messageKey);
				} catch (final MissingResourceException e) {
					// at this point all options exhausted, return messageKey itself
					resolvedMessage = messageKey;
				}
			}
		}

		// cache resolved message
		if (cacheMessages) {
			final String cachedResolvedMessage = resolvedMessages.putIfAbsent(localisedMessage, resolvedMessage);
			if (cachedResolvedMessage != null) {
				resolvedMessage = cachedResolvedMessage;
			}
		}

		return resolvedMessage;
	}

	/**
	 * Runs the message interpolation according to algorithm specified in JSR 303. <br/>
	 * Note: <br/>
	 * Look-ups in user bundles is recursive whereas look-ups in default bundle are not!
	 *
	 * @param message the message to interpolate
	 * @param annotationParameters the parameters of the annotation for which to interpolate this message
	 * @param locale the {@code Locale} to use for the resource bundle.
	 *
	 * @return the interpolated message.
	 */
	private String interpolateMessage(final String message, final Map<String, Object> annotationParameters, final Locale locale) {
		final LocalisedMessage localisedMessage = new LocalisedMessage(message, locale);
		String resolvedMessage = null;

		if (cacheMessages) {
			resolvedMessage = resolvedMessages.get(localisedMessage);
		}

		// if the message is not already in the cache we have to run step 1-3 of the message resolution
		if (resolvedMessage == null) {
			final ResourceBundle defaultResourceBundle = defaultResourceBundleLocator.getResourceBundle(locale);

			String userBundleResolvedMessage = null;
			resolvedMessage = message;
			boolean evaluatedDefaultBundleOnce = false;
			do {
				// search the user bundle recursive (step1)
				for (final ResourceBundleLocator userResourceBundleLocator : userResourceBundleLocators) {
					final ResourceBundle userResourceBundle = userResourceBundleLocator.getResourceBundle(locale);
					userBundleResolvedMessage = replaceVariables(resolvedMessage, userResourceBundle, locale, true);
					if (userBundleResolvedMessage != null && !userBundleResolvedMessage.equals(message)) {
						break;
					}
				}

				// exit condition - we have at least tried to validate against the default bundle and there was no
				// further replacements
				if (evaluatedDefaultBundleOnce && !hasReplacementTakenPlace(userBundleResolvedMessage, resolvedMessage)) {
					break;
				}

				// search the default bundle non recursive (step2)
				resolvedMessage = replaceVariables(userBundleResolvedMessage, defaultResourceBundle, locale, false);
				evaluatedDefaultBundleOnce = true;
			} while (true);
		}

		// cache resolved message
		if (cacheMessages) {
			final String cachedResolvedMessage = resolvedMessages.putIfAbsent(localisedMessage, resolvedMessage);
			if (cachedResolvedMessage != null) {
				resolvedMessage = cachedResolvedMessage;
			}
		}

		// resolve annotation attributes (step 4)
		resolvedMessage = replaceAnnotationAttributes(resolvedMessage, annotationParameters);

		// last but not least we have to take care of escaped literals
		resolvedMessage = resolvedMessage.replace("\\{", "{");
		resolvedMessage = resolvedMessage.replace("\\}", "}");
		resolvedMessage = resolvedMessage.replace("\\\\", "\\");
		return resolvedMessage;
	}

	/**
	 * Checks for replacement taken place.
	 *
	 * @param origMessage the orig message
	 * @param newMessage the new message
	 * @return true, if successful
	 */
	private boolean hasReplacementTakenPlace(final String origMessage, final String newMessage) {
		return !origMessage.equals(newMessage);
	}

	/**
	 * Replace variables.
	 *
	 * @param message the message
	 * @param bundle the bundle
	 * @param locale the locale
	 * @param recurse the recurse
	 * @return the string
	 */
	private String replaceVariables(final String message, final ResourceBundle bundle, final Locale locale, final boolean recurse) {
		final Matcher matcher = MESSAGE_PARAMETER_PATTERN.matcher(message);
		final StringBuffer stringBuffer = new StringBuffer();
		String resolvedParameterValue;
		while (matcher.find()) {
			final String parameter = matcher.group(1);
			resolvedParameterValue = resolveParameter(parameter, bundle, locale, recurse);

			matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(resolvedParameterValue));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	/**
	 * Replace annotation attributes.
	 *
	 * @param message the message
	 * @param annotationParameters the annotation parameters
	 * @return the string
	 */
	private String replaceAnnotationAttributes(final String message, final Map<String, Object> annotationParameters) {
		final Matcher matcher = MESSAGE_PARAMETER_PATTERN.matcher(message);
		final StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String resolvedParameterValue;
			final String parameter = matcher.group(1);
			final Object variable = annotationParameters.get(removeCurlyBrace(parameter));
			if (variable != null) {
				resolvedParameterValue = variable.toString();
			} else {
				resolvedParameterValue = parameter;
			}
			resolvedParameterValue = Matcher.quoteReplacement(resolvedParameterValue);
			matcher.appendReplacement(stringBuffer, resolvedParameterValue);
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	/**
	 * Resolve parameter.
	 *
	 * @param parameterName the parameter name
	 * @param bundle the bundle
	 * @param locale the locale
	 * @param recurse the recurse
	 * @return the string
	 */
	private String resolveParameter(final String parameterName, final ResourceBundle bundle, final Locale locale,
			final boolean recurse) {
		String parameterValue;
		try {
			if (bundle != null) {
				parameterValue = bundle.getString(removeCurlyBrace(parameterName));
				if (recurse) {
					parameterValue = replaceVariables(parameterValue, bundle, locale, recurse);
				}
			} else {
				parameterValue = parameterName;
			}
		} catch (final MissingResourceException e) {
			// return parameter itself
			parameterValue = parameterName;
		}
		return parameterValue;
	}

	/**
	 * Removes the curly brace.
	 *
	 * @param parameter the parameter
	 * @return the string
	 */
	private String removeCurlyBrace(final String parameter) {
		return parameter.substring(1, parameter.length() - 1);
	}

	/**
	 * The Class LocalisedMessage.
	 */
	protected static final class LocalisedMessage {

		/** The message. */
		private final String message;

		/** The locale. */
		private final Locale locale;

		/**
		 * Instantiates a new localised message.
		 *
		 * @param message the message
		 * @param locale the locale
		 */
		LocalisedMessage(final String message, final Locale locale) {
			this.message = message;
			this.locale = locale;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}

			// jshrader this is code imported from hibernate, we aren't altering this aspect of this code
			// furthermore, this is a perfect place where inline conditional is prefered as the code is actually
			// more condensed and easier to read
			// CHECKSTYLE:OFF
			if (object == null || getClass() != object.getClass()) {
				return false;
			}
			// CHECKSTYLE:ON

			final LocalisedMessage that = (LocalisedMessage) object;

			// jshrader this is code imported from hibernate, we aren't altering this aspect of this code
			// furthermore, this is a perfect place where inline conditional is prefered as the code is actually
			// more condensed and easier to read
			// CHECKSTYLE:OFF
			if (locale != null ? !locale.equals(that.locale) : that.locale != null) {
				return false;
			}
			if (message != null ? !message.equals(that.message) : that.message != null) {
				return false;
			}
			// CHECKSTYLE:ON

			return true;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			// jshrader this is code imported from hibernate, we aren't altering this aspect of this code
			// furthermore, this is a perfect place where inline conditional is prefered as the code is actually
			// more condensed and easier to read
			// CHECKSTYLE:OFF
			int result = message != null ? message.hashCode() : 0;
			result = 31 * result + (locale != null ? locale.hashCode() : 0);
			return result;
			// CHECKSTYLE:ON
		}
	}
}
