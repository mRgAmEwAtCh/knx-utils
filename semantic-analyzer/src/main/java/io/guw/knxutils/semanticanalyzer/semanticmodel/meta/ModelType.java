package io.guw.knxutils.semanticanalyzer.semanticmodel.meta;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.GenericCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.*;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum ModelType {
    LIGHT(Light.class.getAnnotation(KnxIdentifier.class)),
    DIMMABLE_LIGHT(DimmableLight.class.getAnnotation(KnxIdentifier.class)),
    POWER_OUTLET(PowerOutlet.class.getAnnotation(KnxIdentifier.class)),
    SHUTTER(Shutter.class.getAnnotation(KnxIdentifier.class)),
    BLINDS(Blinds.class.getAnnotation(KnxIdentifier.class)),
    ;
    private static final Logger LOG = LoggerFactory.getLogger(ModelType.class);
    @Getter
    private final Set<String> terms;
    @Getter
    private final List<String> prefix;
    @Getter
    private final List<String> description;


    ModelType(KnxIdentifier knxIdentifier) {
        this.terms = Set.of(knxIdentifier.terms());
        this.prefix = Arrays.asList(knxIdentifier.prefixes());
        this.description = Arrays.asList(knxIdentifier.description());
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
