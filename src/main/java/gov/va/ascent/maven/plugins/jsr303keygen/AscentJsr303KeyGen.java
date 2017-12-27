package gov.va.ascent.maven.plugins.jsr303keygen;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Mojo( name = "keygen")
public class AscentJsr303KeyGen extends AbstractMojo {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AscentJsr303KeyGen.class);

    @Parameter(property = "keygen.ascentJsr303KeyGenDescriptorFile")
    private File ascentJsr303KeyGenDescriptorFile;

    @Parameter(property = "keygen.ascentJsr303KeyGenOutputFile")
    private File ascentJsr303KeyGenOutputFile;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( ascentJsr303KeyGenDescriptorFile.getAbsolutePath() );
        getLog().info( ascentJsr303KeyGenOutputFile.getAbsolutePath() );

    }
}
