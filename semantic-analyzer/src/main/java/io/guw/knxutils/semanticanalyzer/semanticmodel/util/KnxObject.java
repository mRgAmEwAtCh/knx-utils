package io.guw.knxutils.semanticanalyzer.semanticmodel.util;

import java.util.Arrays;
import java.util.List;

public class KnxObject {
    private KnxIdentifier getKnxIdentifier() {
        return this.getClass().getAnnotation(KnxIdentifier.class);
    }
    public List<String> getTerms(){
        return Arrays.asList(getKnxIdentifier().terms());
    }
    public List<String> getPrefixes(){
        return Arrays.asList(getKnxIdentifier().prefixes());
    }
    public List<String> getDescriptions(){
        return Arrays.asList(getKnxIdentifier().description());
    }
}
