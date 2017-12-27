package gov.va.ascent.maven.plugins.jsr303keygen.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.InterfaceBasedErrorKeys;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.TestModel1;
import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.TestModel4;

public class JsonModelMapper_UnitTest {

	@Test
	public void testDescriptorParsing_ErrorHandling() throws IOException{
		//try to convert null object into JSON
		Assert.assertNull(JsonModelMapper.descriptorToJson(null));
		
		//test corrupt JSON handling
		Assert.assertNotNull(JsonModelMapper.descriptorFromJson("{\"AscentJsr303KeyGenDescriptor\" : { }}"));
		Assert.assertNull(JsonModelMapper.descriptorFromJson(null));
		Assert.assertNull(JsonModelMapper.descriptorFromJson("\"AscentJsr303KeyGenDescriptor\" : { }}"));
		Assert.assertNull(JsonModelMapper.descriptorFromJson("{\"AscentJsr303KeyGenDescriptor\" : { \"badField\" : \"notAllowed\" }}"));
	}
	
	@Test
	public void testDescriptorParsing() throws IOException, URISyntaxException{
		AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptor = getSampleAscentJsr303KeyGenDescriptor();
		
		//to JSON conversion
		String wssJsr303KeyGenDescriptorJson = JsonModelMapper.descriptorToJson(ascentJsr303KeyGenDescriptor);
		Assert.assertNotNull(wssJsr303KeyGenDescriptorJson);
		System.out.println(wssJsr303KeyGenDescriptorJson);
		
		//from JSON conversion using JSON we just created
		AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptorFromJson = JsonModelMapper.descriptorFromJson(wssJsr303KeyGenDescriptorJson);
		Assert.assertNotNull(ascentJsr303KeyGenDescriptorFromJson);
		Assert.assertEquals(ascentJsr303KeyGenDescriptor, ascentJsr303KeyGenDescriptorFromJson);
		
		//from JSON conversion using file
		URL sampleJsonFileURL = this.getClass().getResource("/exampleJson_AscentJsr303KeyGenDescriptor.txt");
		File sampleJsonFile = new File(sampleJsonFileURL.toURI());	
		ascentJsr303KeyGenDescriptorFromJson = JsonModelMapper.descriptorFromJson(FileUtils.readFileToString(sampleJsonFile));
		Assert.assertNotNull(ascentJsr303KeyGenDescriptorFromJson);
		Assert.assertEquals(ascentJsr303KeyGenDescriptor, ascentJsr303KeyGenDescriptorFromJson);
	}
	
	@Test
	public void testConstructorToMakeCoverageHappy(){
		//basically assert no exceptions
		Assert.assertNotNull(new JsonModelMapper());
	}
	
	/**
	 * Gets the sample AscentJsr303KeyGenDescriptor for usage in tests.
	 *
	 * @return the sample wss jsr 303 key gen descriptor
	 */
	private AscentJsr303KeyGenDescriptor getSampleAscentJsr303KeyGenDescriptor() {
		//setup the ClassDescriptor objects
		OperationDescriptor operationDescriptor = new OperationDescriptor();
		operationDescriptor.setName("/sample/operation/name1");
		operationDescriptor.setDescription("description");
		Map<String,String> errorKeys = new HashMap<String, String>();
		errorKeys.put("key1", "key1 description");
		errorKeys.put("key2", "key2 description");
		operationDescriptor.setErrorKeys(errorKeys);
		Set<String> genJSR303KeysFromClasses = new HashSet<String>();
		genJSR303KeysFromClasses.add(TestModel1.class.getName());
		genJSR303KeysFromClasses.add(TestModel4.class.getName());
		operationDescriptor.setGenJsr303KeysFromClasses(genJSR303KeysFromClasses);
		Set<String> genKeysFromInterfaces = new HashSet<String>();
		genKeysFromInterfaces.add(InterfaceBasedErrorKeys.class.getName());
		operationDescriptor.setGenKeysFromInterfaces(genKeysFromInterfaces);
		
		//setup the AscentJsr303KeyGenDescriptor object
		AscentJsr303KeyGenDescriptor ascentJsr303KeyGenDescriptor = new AscentJsr303KeyGenDescriptor();
		LinkedHashSet<OperationDescriptor> operationDescriptors = new LinkedHashSet<OperationDescriptor>();
		operationDescriptors.add(operationDescriptor);
		ascentJsr303KeyGenDescriptor.setOperationDescriptors(operationDescriptors);
		return ascentJsr303KeyGenDescriptor;
	}
	
}
