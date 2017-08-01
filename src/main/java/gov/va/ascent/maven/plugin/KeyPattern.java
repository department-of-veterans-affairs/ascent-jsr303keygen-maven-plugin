package gov.va.ascent.maven.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by vgadda on 7/25/17.
 */
public class KeyPattern {

    private static final String[] REGEX_PARTS = { "*", "$", "^", "+" };
    private List<Pattern> propertyPatterns;

    public KeyPattern(List<String> propertyKeys){
        this.propertyPatterns = regexPropertyKeys(propertyKeys);
    }

    public boolean keyMatchesPattern(String key){
        for (Pattern pattern : this.propertyPatterns) {
            if (pattern.matcher(key).matches()) {
                return true;
            }
        }
        return false;
    }

    private List<Pattern> regexPropertyKeys(List<String> propertyKeys) {
        List<Pattern> propertyKeyPatterns = new ArrayList<>();
        for(String propertyKey: propertyKeys){
            propertyKeyPatterns.add(getPattern(propertyKey)) ;
        }

        return propertyKeyPatterns;
    }

    private Pattern getPattern(String value) {
        if (isRegex(value)) {
            return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
        return Pattern.compile(".*" + value + ".*", Pattern.CASE_INSENSITIVE);
    }

    private boolean isRegex(String value) {
        for (String part : REGEX_PARTS) {
            if (value.contains(part)) {
                return true;
            }
        }
        return false;
    }

    public List<Pattern> getPropertyPatterns() {
        return propertyPatterns;
    }
}
