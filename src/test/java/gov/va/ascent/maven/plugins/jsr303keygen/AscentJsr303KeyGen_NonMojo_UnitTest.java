package gov.va.ascent.maven.plugins.jsr303keygen;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AscentJsr303KeyGen_NonMojo_UnitTest {

	private static final String DESCRIPTOR_FILE_PATH_GOOD = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor.txt";
	private static final String DESCRIPTOR_FILE_PATH_INVALIDCLASS_JSON =
			"src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_InvalidClass.txt";
	private static final String DESCRIPTOR_FILE_PATH_INVALIDINTERFACE_JSON =
			"src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_InvalidInterface.txt";
	private static final String DESCRIPTOR_FILE_PATH_MISSING =
			"src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_DOES_NOT_EXIST.txt";
	private static final String DESCRIPTOR_FILE_PATH_EMPTY = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_Empty.txt";

	private static final String OUTPUT_FILE_PATH_GOOD_ERROR_KEYS = "target/ext-docs/errorkeys.html";
	private static final String OUTPUT_FILE_PATH_BAD = "target/../target";

	private File ascentJsr303KeyGenDescriptorFile;
	private File ascentJsr303KeyGenOutputFile;

	@Before
	public void before() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_GOOD);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
	}

	@Test
	public void testDescriptorFile_Null() {
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
		try {
			AscentJsr303KeyGen.doExecute(null, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(wre.getMessage().contains("ascentJsr303KeyGenDescriptorFile input file is required"));
		}
	}

	@Test
	public void testDescriptorFile_InvalidClass() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_INVALIDCLASS_JSON);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(
					wre.getMessage().contains("ClassNotFoundException getting Jsr303 for class, verify your configuration."));
		}
	}

	@Test
	public void testDescriptorFile_InvalidInterface() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_INVALIDINTERFACE_JSON);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(
					wre.getMessage().contains("ClassNotFoundException getting keys from interface, verify your configuration."));
		}
	}

	@Test
	public void testDescriptorFile_Missing() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_MISSING);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(wre.getMessage().contains("IOException reading the ascentJsr303KeyGenDescriptorFile: "));
		}
	}

	@Test
	public void testDescriptorFile_Empty() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_EMPTY);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_GOOD_ERROR_KEYS);
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(wre.getMessage()
					.contains("There was an error parsing the ascentJsr303KeyGenDescriptorFile, ensure JSON is valid!"));
		}
	}

	@Test
	public void testOutputFile_Null() {
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, null);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(wre.getMessage().contains("ascentJsr303KeyGenOutputFile input file is a required param"));
		}
	}

	@Test
	public void testOutputFile_IOException() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(DESCRIPTOR_FILE_PATH_GOOD);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(OUTPUT_FILE_PATH_BAD);
		try {
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch (final MojoExecutionException wre) {
			Assert.assertTrue(wre.getMessage().contains("IOException writing the ascentJsr303KeyGenOutputFile"));
		}
	}
}