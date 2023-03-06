package io.guw.knxutils.semanticanalyzer.semanticmodel.model;


import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SemanticIdentifier(
        terms = {"jalousie", "storen", "raff"},
        prefixes = {"J_"},
        description = {"[Jalousie]"}
)
@Getter
@ToString
@SuperBuilder
public class Blinds extends Shutter{
    @NonNull
    private final GroupAddress positionSlateGroupAddress;
    @NonNull
    private final GroupAddress shadowGroupAddress;
    @NonNull
    private final GroupAddress statusPositionSlateGroupAddress;
}
