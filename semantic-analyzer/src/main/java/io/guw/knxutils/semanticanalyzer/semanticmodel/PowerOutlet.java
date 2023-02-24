package io.guw.knxutils.semanticanalyzer.semanticmodel;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxObject;
import lombok.Data;

@Data
@KnxIdentifier(
        terms = {"dose", "steckdose", "strom"},
        prefixes = {"S_"}
)
public class PowerOutlet extends KnxObject {
    private final String name;
    private final GroupAddress primarySwitchGroupAddress;
    private final GroupAddress statusGroupAddress;
}