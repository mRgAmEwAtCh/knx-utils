package io.guw.knxutils.semanticanalyzer.categories.util;

import java.util.Arrays;
import java.util.List;

public class KnxObject {
    public List<String> getTerms(){
        return Arrays.asList(this.getClass().getAnnotation(KnxIdentifier.class).terms());
    }
    public List<String> getPrefixes(){
        return Arrays.asList(this.getClass().getAnnotation(KnxIdentifier.class).prefixes());
    }
    public List<String> getDescriptions(){
        return Arrays.asList(this.getClass().getAnnotation(KnxIdentifier.class).description());
    }
}
