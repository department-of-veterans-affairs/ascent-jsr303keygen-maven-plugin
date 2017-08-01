package gov.va.ascent.maven.plugin;

import gov.va.ascent.maven.plugin.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vgadda on 4/6/17.
 */
public class ConfigMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void testMojo() throws Exception {
        final File project = resources.getBasedir("");
        File pom = new File(project, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());

        ConfigMojo encryptConfig = (ConfigMojo) this.mojoRule.lookupMojo("encryptconfig", pom);

        Assert.assertNotNull(encryptConfig);
        encryptConfig.execute();
        assertEncryption(encryptConfig);
        copyFiles(encryptConfig.getResourceLocation());
    }

    private void assertEncryption(ConfigMojo encryptConfig){
        Yaml yaml = new Yaml();
        try {
            for(String fileName: FileUtil.getFiles(encryptConfig.getResourceLocation())){
                List<Object> yamls = new ArrayList<>();
                for(Object data: yaml.loadAll(new FileSystemResource(fileName).getInputStream())){
                    parseYml(data, encryptConfig);
                }
            }
        }catch (final IOException ex){
            encryptConfig.getLog().error("Error reading files:" + ex);
        }
    }



    private void parseYml(Object object, ConfigMojo encryptConfig) {
        if (!(object instanceof Map)) {
            return;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                parseYml(value, encryptConfig);
            }
            if (key instanceof CharSequence) {
                if(encryptConfig.getPropertyKeyPattern().keyMatchesPattern(key.toString())){
                    encryptConfig.getLog().info("Encrypted Keys: " + key);
                    Assert.assertTrue(value.toString().contains("{cipher}"));
                }
            }
        });

    }


    private void copyFiles(String resourceLocation) {
        try {
            FileUtils.copyDirectory(new File(resourceLocation.replace("testconfig", "originalconfig")), new File(resourceLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
