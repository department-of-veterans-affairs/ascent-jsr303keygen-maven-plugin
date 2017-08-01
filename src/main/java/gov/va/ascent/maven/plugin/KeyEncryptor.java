package gov.va.ascent.maven.plugin;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

import java.io.IOException;

/**
 * Created by vgadda on 7/25/17.
 */
public class KeyEncryptor {

    private TextEncryptor encryptor;
    private String cipher = "{cipher}";

    public KeyEncryptor(String publicKeyLocation){
        setKeyEncryptor(publicKeyLocation);
    }

    private void setKeyEncryptor(String publicKeyLocation){
        String publicKey = null;
        try {
            publicKey = IOUtils.toString (new ClassPathResource(publicKeyLocation).getInputStream());

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        this.encryptor = new RsaSecretEncryptor(publicKey);
    }

    public String encrypt(String plainString){
        return cipher + encryptor.encrypt(plainString);
    }

    public boolean isEncrypted(String value){
        return value.contains(cipher);
    }
}
