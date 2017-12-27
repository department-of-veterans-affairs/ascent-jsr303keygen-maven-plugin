package gov.va.ascent.maven.plugins.jsr303keygen;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class AscentJsr303KeyGen_NonMojo_UnitTest {

	private static final String GOOD_DESCRIPTOR_FILE_PATH = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor.txt";
	private static final String INVALIDCLASS_JSON_DESCRIPTOR_FILE_PATH = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_InvalidClass.txt";
	private static final String INVALIDINTERFACE_JSON_DESCRIPTOR_FILE_PATH = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_InvalidInterface.txt";
	private static final String MISSING_DESCRIPTOR_FILE_PATH = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_DOES_NOT_EXIST.txt";
	private static final String EMPTY_DESCRIPTOR_FILE_PATH = "src/test/resources/exampleJson_AscentJsr303KeyGenDescriptor_Empty.txt";
	
	private static final String GOOD_ERROR_KEYS_OUTPUT_FILE_PATH = "target/ext-docs/errorkeys.html";
	
	private File ascentJsr303KeyGenDescriptorFile;
	private File ascentJsr303KeyGenOutputFile;
	
	@Before
	public void before(){
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(GOOD_DESCRIPTOR_FILE_PATH);
		ascentJsr303KeyGenOutputFile = FileUtils.getFile(GOOD_ERROR_KEYS_OUTPUT_FILE_PATH);
	}
	
	@Test
	public void testNullDescriptorFile() {
		try{
			AscentJsr303KeyGen.doExecute(null, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("ascentJsr303KeyGenDescriptorFile input file is required"));
		}
	}
	
	@Test
	public void testInvalidClassDescriptorFile() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(INVALIDCLASS_JSON_DESCRIPTOR_FILE_PATH);
		try{
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("ClassNotFoundException getting Jsr303 for class, verify your configuration."));
		}
	}
	
	@Test
	public void testInvalidInterfaceDescriptorFile() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(INVALIDINTERFACE_JSON_DESCRIPTOR_FILE_PATH);
		try{
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("ClassNotFoundException getting keys from interface, verify your configuration."));
		}
	}
	
	@Test
	public void testMissingDescriptorFile() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(MISSING_DESCRIPTOR_FILE_PATH);
		try{
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("IOException reading the ascentJsr303KeyGenDescriptorFile: "));
		}
	}
	
	@Test
	public void testEmptyDescriptorFile() {
		ascentJsr303KeyGenDescriptorFile = FileUtils.getFile(EMPTY_DESCRIPTOR_FILE_PATH);
		try{
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, ascentJsr303KeyGenOutputFile);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("There was an error parsing the ascentJsr303KeyGenDescriptorFile, ensure JSON is valid!"));
		}
	}
	
	@Test
	public void testNullOutputFile() {
		try{
			AscentJsr303KeyGen.doExecute(ascentJsr303KeyGenDescriptorFile, null);
			Assert.fail("exception expected");
		} catch(MojoExecutionException wre){
			Assert.assertTrue(wre.getMessage().contains("ascentJsr303KeyGenOutputFile input file is a required param"));
		}
	}
	
	
	
}