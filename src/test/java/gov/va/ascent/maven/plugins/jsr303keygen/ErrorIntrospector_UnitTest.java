package gov.va.ascent.maven.plugins.jsr303keygen;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.InterfaceBasedErrorKeys;
import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.TestModel1;
import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.TestModel4;
import org.junit.Assert;

public class ErrorIntrospector_UnitTest {

	@Test
	public void introspectInterfaceForKeys() throws URISyntaxException, IOException, MojoExecutionException {
		final SortedMap<String, String> errors = new TreeMap<String, String>();
		ErrorIntrospector.introspectInterfaceForKeys(new MessageInterpolator(), InterfaceBasedErrorKeys.class, errors);
		for (final Map.Entry<String, String> error : errors.entrySet()) {
			System.out.println(error.getKey() + "::" + error.getValue());
		}
		Assert.assertEquals(2, errors.size());

		final URL urlToExpectedMessageFile = this.getClass().getResource("/expectedMessages_InterfaceBasedErrorKeys.txt");
		final File expectedMessagesFile = new File(urlToExpectedMessageFile.toURI());
		final List<String> expextedMessages = FileUtils.readLines(expectedMessagesFile, Charset.defaultCharset());
		Assert.assertEquals(2, expextedMessages.size());
		for (final String expectedMessage : expextedMessages) {
			Assert.assertTrue(errors.containsKey(expectedMessage.split("::")[0]));
			final String message = errors.get(expectedMessage.split("::")[0]);
			if (message != "") {
				Assert.assertEquals(message, expectedMessage.split("::")[1]);
			}
		}
	}

	/**
	 * Instrospect class test for TestModel1. TestModel1 contains TestModel2 which contains TestModel3 so this tests various things
	 * down that flow.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	@Test
	public void instrospectClassForJSR303s_TestModel1() throws IOException, URISyntaxException {
		final String nodepath = "";
		final SortedMap<String, String> jsr303Errors = new TreeMap<String, String>();
		ErrorIntrospector.instrospectClassForJSR303s(new MessageInterpolator(), TestModel1.class, nodepath, jsr303Errors);
		System.out.println("MESSAGE COUNT: " + jsr303Errors.size());
		for (final Map.Entry<String, String> jsr303Error : jsr303Errors.entrySet()) {
			System.out.println(jsr303Error.getKey() + "::" + jsr303Error.getValue());
		}
		Assert.assertEquals(43, jsr303Errors.size());

		final URL urlToExpectedMessageFile = this.getClass().getResource("/expectedMessages_TestModel1.txt");
		final File expectedMessagesFile = new File(urlToExpectedMessageFile.toURI());
		final List<String> expextedMessages = FileUtils.readLines(expectedMessagesFile, Charset.defaultCharset());
		Assert.assertEquals(43, expextedMessages.size());
		for (final String expectedMessage : expextedMessages) {
			Assert.assertTrue(jsr303Errors.containsKey(expectedMessage.split("::")[0]));
			final String message = jsr303Errors.get(expectedMessage.split("::")[0]);
			if (message != "") {
				Assert.assertEquals(message, expectedMessage.split("::")[1]);
			}
		}
	}

	/**
	 * Instrospect class test for TestModel4. TestModel4 contains TestModel5 so this tests various things down that flow.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	@Test
	public void instrospectClassForJSR303s_TestModel4() throws IOException, URISyntaxException {
		final String nodepath = "";
		final SortedMap<String, String> jsr303Errors = new TreeMap<String, String>();
		ErrorIntrospector.instrospectClassForJSR303s(new MessageInterpolator(), TestModel4.class, nodepath, jsr303Errors);
		System.out.println("MESSAGE COUNT: " + jsr303Errors.size());
		for (final Map.Entry<String, String> jsr303Error : jsr303Errors.entrySet()) {
			System.out.println(jsr303Error.getKey() + "::" + jsr303Error.getValue());
		}
		Assert.assertEquals(22, jsr303Errors.size());

		final URL urlToExpectedMessageFile = this.getClass().getResource("/expectedMessages_TestModel4.txt");
		final File expectedMessagesFile = new File(urlToExpectedMessageFile.toURI());
		final List<String> expextedMessages = FileUtils.readLines(expectedMessagesFile, Charset.defaultCharset());
		Assert.assertEquals(22, expextedMessages.size());
		for (final String expectedMessage : expextedMessages) {
			Assert.assertTrue(jsr303Errors.containsKey(expectedMessage.split("::")[0]));
			final String message = jsr303Errors.get(expectedMessage.split("::")[0]);
			if (message != "") {
				Assert.assertEquals(message, expectedMessage.split("::")[1]);
			}
		}
	}

	@Test
	public void testConstructorToMakeCoverageHappy() {
		// basically assert no exceptions
		Assert.assertNotNull(new ErrorIntrospector());
	}
}
