package io.guw.knxutils.semanticanalyzer.categories;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.categories.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.categories.util.KnxObject;
import lombok.Data;

@Data
@KnxIdentifier(
        terms = {"rollo", "rolladen", "jalousie", "storen", "raff"},
        prefixes = {"J_", "R_"}
)
public class Shutter extends KnxObject {
    private final String name;
    private final GroupAddress primarySwitchGroupAddress;
    private final GroupAddress statusGroupAddress;
}
