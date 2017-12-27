package gov.va.ascent.maven.plugins.jsr303keygen.model;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The Class JsonModelMapper contains utility operations to convert to/from JSON our custom model objects we use to describe the
 * generation process.
 *
 * @author jshrader
 */
public final class JsonModelMapper {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonModelMapper.class);

	/** The Constant EXCEPTION_LOG_SPACER. */
	private static final String EXCEPTION_LOG_SPACER = " ";

	/** The Constant MAPPER. */
	private static final ObjectMapper MAPPER = new ObjectMapper();
	static {
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		MAPPER.enable(SerializationFeature.WRAP_ROOT_VALUE);
		MAPPER.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		MAPPER.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * Hidden utility class constructor.
	 */
	protected JsonModelMapper() {
		super();
	}

	/**
	 * Descriptor to json.
	 *
	 * @param descriptor the descriptor
	 * @return the string
	 */
	public static String descriptorToJson(final AscentJsr303KeyGenDescriptor descriptor) {
		if (descriptor != null) {
			try {
				return JsonModelMapper.MAPPER.writeValueAsString(descriptor);
			} catch (final JsonProcessingException jpe) {
				LOGGER.error(
						"descriptorToJson resulted in error, input param and exception : " + descriptor + EXCEPTION_LOG_SPACER + jpe);
			}
		}
		return null;
	}

	/**
	 * Descriptor from json.
	 *
	 * @param json the json
	 * @return the ascent jsr 303 key gen descriptor
	 */
	public static AscentJsr303KeyGenDescriptor descriptorFromJson(final String json) {
		if (json != null) {
			try {
				return JsonModelMapper.MAPPER.readValue(json, AscentJsr303KeyGenDescriptor.class);
			} catch (final JsonProcessingException jpe) {
				LOGGER.error("descriptorFromJson resulted in JsonProcessingException, input JSON and exception : " + json
						+ EXCEPTION_LOG_SPACER + jpe);
			} catch (final IOException ioe) {
				LOGGER.error(
						"descriptorFromJson resulted in IOException, input JSON and exception : " + json + EXCEPTION_LOG_SPACER + ioe);
			}
		}
		return null;
	}
}
