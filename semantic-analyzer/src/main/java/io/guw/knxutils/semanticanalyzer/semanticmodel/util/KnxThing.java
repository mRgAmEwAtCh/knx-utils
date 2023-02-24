package io.guw.knxutils.semanticanalyzer.semanticmodel.util;

import static java.util.Arrays.asList;
import java.util.List;

public class KnxThing {
    private KnxIdentifier getKnxIdentifier() {
        return this.getClass().getAnnotation(KnxIdentifier.class);
    }
    public List<String> getTerms(){
        return asList(getKnxIdentifier().terms());
    }
    public List<String> getPrefixes(){
        return asList(getKnxIdentifier().prefixes());
    }
    public List<String> getDescriptions(){
        return asList(getKnxIdentifier().description());
    }
}
