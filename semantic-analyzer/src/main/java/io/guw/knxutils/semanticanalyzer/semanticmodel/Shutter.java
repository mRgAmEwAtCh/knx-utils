package io.guw.knxutils.semanticanalyzer.semanticmodel;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxThing;
import lombok.Data;

@Data
@KnxIdentifier(
        terms = {"rollo", "rolladen", "jalousie", "storen", "raff"},
        prefixes = {"J_", "R_"}
)
public class Shutter extends KnxThing {
    private final String name;
    private final GroupAddress primarySwitchGroupAddress;
    private final GroupAddress statusGroupAddress;
}
