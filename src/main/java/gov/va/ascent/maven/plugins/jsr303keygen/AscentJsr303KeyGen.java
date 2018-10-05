package gov.va.ascent.maven.plugins.jsr303keygen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.maven.plugins.jsr303keygen.model.AscentJsr303KeyGenDescriptor;
import gov.va.ascent.maven.plugins.jsr303keygen.model.JsonModelMapper;
import gov.va.ascent.maven.plugins.jsr303keygen.model.OperationDescriptor;

/**
 * The Class AscentJsr303KeyGen is the Maven plugin mojo itself, the entry point when executed.
 *
 * @author jshrader
 */
@Mojo(name = "keygen", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class AscentJsr303KeyGen extends AbstractMojo {

	/** The Constant LOGGER. */
	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(AscentJsr303KeyGen.class);

	@Parameter(property = "keygen.ascentJsr303KeyGenDescriptorFile")
	private File ascentJsr303KeyGenDescriptorFile;

	@Parameter(property = "keygen.ascentJsr303KeyGenOutputFile")
	private File ascentJsr303KeyGenOutputFile;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public final void execute() throws MojoExecutionException {
		doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
	}

	/**
	 * Do execute, broken out from execute to enable easier testing.
	 *
	 * @param ascentJsr303KeyGenDescriptorFile the ascent jsr 303 key gen descriptor file
	 * @param ascentJsr303KeyGenOutputFile the ascent jsr 303 key gen output file
	 */
	protected static void doExecute(final File ascentJsr303KeyGenDescriptorFile, final File ascentJsr303KeyGenOutputFile)
			throws MojoExecutionException {
		if (ascentJsr303KeyGenDescriptorFile == null) {
			throw new MojoExecutionException("ascentJsr303KeyGenDescriptorFile input file is required");
		} else {
			LOGGER.info(
					"Attempting to use ascentJsr303KeyGenDescriptorFile at: " + ascentJsr303KeyGenDescriptorFile.getAbsolutePath());
		}
		if (ascentJsr303KeyGenOutputFile == null) {
			throw new MojoExecutionException("ascentJsr303KeyGenOutputFile input file is a required param");
		} else {
			LOGGER.info("Attempting to use ascentJsr303KeyGenOutputFile at: " + ascentJsr303KeyGenOutputFile.getAbsolutePath());
			// ensure file is writeable (side effect is empty it out and recreate)
			BufferedWriter writer = null;
			try {
				if (!Files.exists(Paths.get(ascentJsr303KeyGenOutputFile.getParent()))) {
					Files.createDirectories(Paths.get(ascentJsr303KeyGenOutputFile.getParent()));
				}
				writer = Files.newBufferedWriter(Paths.get(ascentJsr303KeyGenOutputFile.getAbsolutePath()));
				writer.write("");
			} catch (final IOException ioe) {
				final String message = "IOException writing the ascentJsr303KeyGenOutputFile!"; // NOSONAR local var is not a
																								 // "constant"
				final MojoExecutionException mojoExecutionException = new MojoExecutionException(message, ioe);
				LOGGER.error(message, mojoExecutionException);
				throw mojoExecutionException;
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}

		AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptor = null;
		try {
			ascentJsr303KeyGenDescriptor =
					JsonModelMapper.descriptorFromJson(new String(Files.readAllBytes(ascentJsr303KeyGenDescriptorFile.toPath())));
		} catch (final IOException ioe) {
			throw new MojoExecutionException("IOException reading the ascentJsr303KeyGenDescriptorFile: ", ioe);
		}
		if (ascentJsr303KeyGenDescriptor == null) {
			throw new MojoExecutionException("There was an error parsing the ascentJsr303KeyGenDescriptorFile, ensure JSON is valid!");
		}

		MessageInterpolator messageInterpolator;
		if (ascentJsr303KeyGenDescriptor.getCustomMessageBundles() != null
				&& !ascentJsr303KeyGenDescriptor.getCustomMessageBundles().isEmpty()) {
			messageInterpolator = new MessageInterpolator(ascentJsr303KeyGenDescriptor.getCustomMessageBundles());
		} else {
			messageInterpolator = new MessageInterpolator();
		}
		LOGGER.info("Loaded and going to use the following ascentJsr303KeyGenDescriptor: " + ascentJsr303KeyGenDescriptor);
		consolidateKeysForGeneration(messageInterpolator, ascentJsr303KeyGenDescriptor);

		LOGGER.info("Writing out the HTML file: " + ascentJsr303KeyGenOutputFile.getAbsolutePath());
		genHtml(ascentJsr303KeyGenDescriptor, ascentJsr303KeyGenOutputFile);
	}

	/**
	 * Consolidate keys for generation.
	 *
	 * Static broken out from execute to enable easier testing.
	 *
	 * @param ascentJsr303KeyGenDescriptor the ascent jsr 303 key gen descriptor
	 */
	private static void consolidateKeysForGeneration(final MessageInterpolator messageInterpolator,
			final AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptor) throws MojoExecutionException {
		for (final OperationDescriptor operationDescriptor : ascentJsr303KeyGenDescriptor.getOperationDescriptors()) {
			getJsr303Keys(messageInterpolator, operationDescriptor);
			getInterfaceKeys(messageInterpolator, operationDescriptor);
			// ensure the consolidated list is all sorted
			operationDescriptor.setErrorKeys(new TreeMap<>(operationDescriptor.getErrorKeys()));
		}
	}

	/**
	 * Gets the interface keys.
	 *
	 * @param operationDescriptor the operation descriptor
	 * @return the interface keys
	 */
	private static void getInterfaceKeys(final MessageInterpolator messageInterpolator,
			final OperationDescriptor operationDescriptor) throws MojoExecutionException {
		// add in all the JSR 303 for the specified classes
		LOGGER.info("Processing interface keys for operation: " + operationDescriptor.getName());
		final Map<String, String> interfaceErrors = new HashMap<>();
		for (final String interfaceToLookAt : operationDescriptor.getGenKeysFromInterfaces()) {
			LOGGER.info("Pulling keys out of source interface: " + interfaceToLookAt);
			try {
				ErrorIntrospector.introspectInterfaceForKeys(messageInterpolator, Class.forName(interfaceToLookAt), interfaceErrors);
			} catch (final ClassNotFoundException cnfe) {
				final String message = "ClassNotFoundException getting keys from interface, " // NOSONAR local var is not a "constant"
						+ "verify your configuration."; // NOSONAR local var is not a "constant"
				final MojoExecutionException mojoExecutionException = new MojoExecutionException(message, cnfe);
				LOGGER.error(message, mojoExecutionException);
				throw mojoExecutionException;
			}
		}
		if (!interfaceErrors.isEmpty()) {
			for (final Map.Entry<String, String> interfaceError : interfaceErrors.entrySet()) {
				operationDescriptor.getErrorKeys().put(interfaceError.getKey(), interfaceError.getValue());
			}
		}
	}

	/**
	 * Gets the jsr 303 keys.
	 *
	 * @param operationDescriptor the operation descriptor
	 * @return the jsr 303 keys
	 */
	private static void getJsr303Keys(final MessageInterpolator messageInterpolator, final OperationDescriptor operationDescriptor)
			throws MojoExecutionException {
		// add in all the JSR 303 for the specified classes
		LOGGER.info("Processing JSR303 keys for operation: " + operationDescriptor.getName());
		final Map<String, String> jsr303Errors = new HashMap<>();
		for (final String jsr303SourceClass : operationDescriptor.getGenJsr303KeysFromClasses()) {
			LOGGER.info("Pulling Jsr303 validations out of source class: " + jsr303SourceClass);
			try {
				ErrorIntrospector.instrospectClassForJSR303s(messageInterpolator, Class.forName(jsr303SourceClass), "", jsr303Errors);
			} catch (final ClassNotFoundException cnfe) {
				final String message = "ClassNotFoundException getting Jsr303 for class, " // NOSONAR local var is not a "constant"
						+ "verify your configuration."; // NOSONAR local var is not a "constant"
				final MojoExecutionException mojoExecutionException = new MojoExecutionException(message, cnfe);
				LOGGER.error(message, mojoExecutionException);
				throw mojoExecutionException;
			}
		}
		if (!jsr303Errors.isEmpty()) {
			for (final Map.Entry<String, String> jsr303Error : jsr303Errors.entrySet()) {
				operationDescriptor.getErrorKeys().put(jsr303Error.getKey(), jsr303Error.getValue());
			}
		}
	}

	/**
	 * Gen html.
	 *
	 * @param ascentJsr303KeyGenDescriptor the ascent jsr 303 key gen descriptor
	 * @param ascentJsr303KeyGenOutputFile the ascent jsr 303 key gen output file
	 */
	private static void genHtml(final AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptor,
			final File ascentJsr303KeyGenOutputFile) throws MojoExecutionException {

		final VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.SystemLogChute");
		velocityEngine.init();

		// create+populate context
		final VelocityContext context = new VelocityContext();
		context.put("ascentJsr303KeyGenDescriptor", ascentJsr303KeyGenDescriptor);

		// load template
		final String templatePath = "templates/errorkeys.vm"; // NOSONAR local var is not a "constant"
		InputStream input = null;
		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/errorkeys.vm");
			if (input == null) {
				throw new MojoExecutionException("HTML template file doesn't exist, plugin won't work properly.");
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
		final Template template = velocityEngine.getTemplate(templatePath, "UTF-8");

		// load buffered writer to output the file
		try (BufferedWriter writer = Files.newBufferedWriter(ascentJsr303KeyGenOutputFile.toPath())) {
			// generate the file
			template.merge(context, writer);
		} catch (final IOException ioe) {
			throw new MojoExecutionException("Failed to open writer for output file.", ioe);
		}
	}

}
