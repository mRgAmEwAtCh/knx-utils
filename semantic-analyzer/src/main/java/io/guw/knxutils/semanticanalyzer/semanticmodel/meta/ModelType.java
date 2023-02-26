package io.guw.knxutils.semanticanalyzer.semanticmodel.meta;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.GenericCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.*;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum ModelType {
    LIGHT(Light.class.getAnnotation(SemanticIdentifier.class)),
    DIMMABLE_LIGHT(DimmableLight.class.getAnnotation(SemanticIdentifier.class)),
    POWER_OUTLET(PowerOutlet.class.getAnnotation(SemanticIdentifier.class)),
    SHUTTER(Shutter.class.getAnnotation(SemanticIdentifier.class)),
    BLINDS(Blinds.class.getAnnotation(SemanticIdentifier.class)),
    ;
    private static final Logger LOG = LoggerFactory.getLogger(ModelType.class);
    @Getter
    private final Set<String> terms;
    @Getter
    private final List<String> prefix;
    @Getter
    private final List<String> description;


    ModelType(SemanticIdentifier semanticIdentifier) {
        this.terms = Set.of(semanticIdentifier.terms());
        this.prefix = Arrays.asList(semanticIdentifier.prefixes());
        this.description = Arrays.asList(semanticIdentifier.description());
    }

    public boolean matchesModelType(GroupAddress ga, GenericCharacteristics.GroupAddressDocument doc){
        return containsTerms(ga, doc) || containsPrefix(ga) || containsDescription(ga);
    }

    private boolean containsTerms(GroupAddress ga, GenericCharacteristics.GroupAddressDocument doc) {
        if (doc == null) {
            LOG.warn("No index available for GA: {}", ga);
            return false;
        }
        return doc.nameTerms.parallelStream().anyMatch(terms::contains);
    }

    private boolean containsPrefix(GroupAddress ga) {
        if ((null == ga.getName()) || ga.getName().isBlank()) {
            LOG.warn("GA with blank/empty name should be fixed: {}", ga);
            return false;
        }
        return prefix.parallelStream().anyMatch((p) -> ga.getName().startsWith(p));
    }

    private boolean containsDescription(GroupAddress ga) {
        if ((null == ga.getName()) || ga.getName().isBlank()) {
            LOG.warn("GA with blank/empty name should be fixed: {}", ga);
            return false;
        }
        return (ga.getDescription() != null) && description.parallelStream().anyMatch(ga.getDescription()::contains);
    }
}
