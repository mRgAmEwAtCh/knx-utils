package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SemanticIdentifier(
        terms = {"dose", "steckdose", "strom"},
        prefixes = {"S_"},
        description = {"[Steckdose]"}
)
@Getter
@ToString
@SuperBuilder
public class PowerOutlet implements Thing{
    @NonNull
    private final String name;
    @NonNull
    private final GroupAddress primarySwitchGroupAddress;
    @NonNull
    private final GroupAddress statusGroupAddress;
}
