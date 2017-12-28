package gov.va.ascent.maven.plugins.jsr303keygen;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;


public class AscentJsr303KeyGen_MojoHappyPath_CustomMessageBundles_UnitTest extends AbstractMojoTestCase {

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		FileUtils.deleteDirectory(new File("target/ext-docs"));

	}

	/** {@inheritDoc} */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @throws Exception if any
	 */
	public void testMojo() throws Exception {
		final File pom = getTestFile("src/test/resources/mojo-happy-path-test/pom_customMessageBundles.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final AscentJsr303KeyGen myMojo = (AscentJsr303KeyGen) lookupMojo("keygen", pom);
		assertNotNull(myMojo);
		
		Assert.assertFalse(new File("target/ext-docs").exists());
		myMojo.execute();
		Assert.assertTrue(new File("target/ext-docs").exists());
		Assert.assertTrue(new File("target/ext-docs/errorkeys.html").exists());
	}
}