package io.guw.knxutils.semanticanalyzer.semanticmodel.util;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static java.util.Arrays.asList;
import java.util.List;

@Getter
@ToString
@SuperBuilder
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
