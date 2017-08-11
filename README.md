# ascent-config-encrypt-plugin

This is a repository for the ascent-config-encrypt-plugin which is a maven plugin implementation that encrypts application properties.

See [Encryption and Decryption](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/Ascent-Config#encryption-and-decryption)

See [Ascent Quick Start Guide](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/Ascent-Quick-Start-Guide) for the software prerequisites.

## Maven plugin to encrypt the properties in yml files

###  Plugin dependency

Add the following snippet to pom.xml to add the plugin dependency.

You can configure plugin to specify:
1. publicKeyLocation - public key to use for encryption.
2. propertyKeys - list of keys to encrypt
3. resourceLocation - location of the yml files to process


         <build>
                <plugins>
                    <plugin>
                        <groupId>gov.va.ascent</groupId>
                        <artifactId>ascent-maven-plugin</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <configuration>
                            <publicKeyLocation>public.txt</publicKeyLocation>
                            <propertyKeys>
                                <propertyKey>password</propertyKey>
                                <propertyKey>secret</propertyKey>
                            </propertyKeys>
                            <resourceLocation>.</resourceLocation>
                        </configuration>
                    </plugin>
                </plugins>
            </build>

### Run plugin

Use the following command to run the plugin

        mvn ascent:encryptconfig

 
