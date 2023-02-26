package io.guw.knxutils.semanticanalyzer.semanticmodel.model;


import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@KnxIdentifier(
        terms = {"jalousie", "storen", "raff"},
        prefixes = {"J_"},
        description = {"[Jalousie]"}
)
@Getter
@ToString
@SuperBuilder
public class Blinds extends Shutter{
    private final GroupAddress positionSlateGroupAddress;
    private final GroupAddress shadowGroupAddress;
    private final GroupAddress statusPositionSlateGroupAddress;
}
