package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SemanticIdentifier(
        terms = {"rollo", "rolladen"},
        prefixes = {"R_"},
        description = {"[Rolladen]"}
)
@Getter
@ToString
@SuperBuilder
public class Shutter implements Thing{
    @NonNull
    private final String name;
    @NonNull
    private final GroupAddress primarySwitchGroupAddress;
    @NonNull
    private final GroupAddress stopGroupAddress;
    @NonNull
    private final GroupAddress positionHeightGroupAddress;
    @NonNull
    private final GroupAddress lockGroupAddress;
    @NonNull
    private final GroupAddress statusPositionHeightGroupAddress;
}
