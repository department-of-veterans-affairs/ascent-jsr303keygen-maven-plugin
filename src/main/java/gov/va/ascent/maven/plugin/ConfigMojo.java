package gov.va.ascent.maven.plugin;

import gov.va.ascent.maven.plugin.util.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.core.io.FileSystemResource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vgadda on 3/29/17.
 */
@Mojo(name = "encryptconfig", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ConfigMojo extends AbstractMojo{

    @Parameter(property = "encryptconfig.resourceLocation")
    private String resourceLocation;

    @Parameter(property = "encryptconfig.propertyKeys")
    private List<String> propertyKeys;

    @Parameter(property = "encryptconfig.publicKeyLocation")
    private String publicKeyLocation;

    private KeyEncryptor encryptor;
    private KeyPattern propertyKeyPattern;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("executing ConfigMojo");
        if(resourceLocation == null){
            throw new RuntimeException("resourceLocation is required");
        }
        if(propertyKeys == null){
            throw new RuntimeException("propertyKeys is required");
        }
        if(publicKeyLocation == null){
            throw new RuntimeException("publicKeyLocation is required");
        }
        initialize();
        parseFiles();
    }

    private void initialize(){
        propertyKeyPattern = new KeyPattern(propertyKeys);
        encryptor = new KeyEncryptor(publicKeyLocation);
    }

    private void parseFiles(){
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try {
            for(String fileName: FileUtil.getFiles(resourceLocation)){
                getLog().info("File:" + fileName);
                List<Object> yamls = new ArrayList<>();
                for(Object data: yaml.loadAll(new FileSystemResource(fileName).getInputStream())){
                    yamls.add(encryptValue(data));
                }
                yaml.dumpAll(yamls.iterator(),new FileWriter(fileName));
            }
        }catch (final IOException ex){
            getLog().error("Issue reading/writing yml file:" + ex);
        }

    }

    private Object encryptValue(Object object) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            return object;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                value = encryptValue(value);
            }
            if (key instanceof CharSequence) {
                if(propertyKeyPattern.keyMatchesPattern(key.toString()) && !encryptor.isEncrypted(value.toString())){
                    getLog().info("Key to encrypt :" + key);
                    result.put(key.toString(), encryptor.encrypt(value.toString()));
                }else {
                    result.put(key.toString(), value);
                }
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        });
        return result;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public KeyPattern getPropertyKeyPattern() {
        return propertyKeyPattern;
    }

}
