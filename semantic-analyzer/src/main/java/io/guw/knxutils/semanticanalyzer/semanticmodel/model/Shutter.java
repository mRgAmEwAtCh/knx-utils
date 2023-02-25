package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxThing;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@KnxIdentifier(
        terms = {"rollo", "rolladen", "jalousie", "storen", "raff"},
        prefixes = {"J_", "R_"},
        description = {"[Rolladen]", "[Jalousie]"}
)
@Getter
@ToString
@SuperBuilder
public class Shutter extends KnxThing {
    private final String name;
    private final GroupAddress primarySwitchGroupAddress;
    private final GroupAddress statusGroupAddress;
}
