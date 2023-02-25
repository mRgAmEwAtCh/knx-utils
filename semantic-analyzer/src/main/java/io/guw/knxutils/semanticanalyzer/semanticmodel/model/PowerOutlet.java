package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxThing;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@KnxIdentifier(
        terms = {"dose", "steckdose", "strom"},
        prefixes = {"S_"},
        description = {"[Steckdose]"}
)
@Getter
@ToString
@SuperBuilder
public class PowerOutlet extends KnxThing {
    private final String name;
    private final GroupAddress primarySwitchGroupAddress;
    private final GroupAddress statusGroupAddress;
}
