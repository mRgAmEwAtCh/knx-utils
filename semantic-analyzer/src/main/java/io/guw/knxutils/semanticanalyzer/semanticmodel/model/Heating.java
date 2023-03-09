package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@SemanticIdentifier(
    terms = {"Heiz"},
    prefixes = {"H_"},
    description = {"[Heizung]"}
)
public class Heating implements Thing{
    @NonNull
    private final String name;
    @NonNull
    private final GroupAddress primarySwitchGroupAddress;
}
