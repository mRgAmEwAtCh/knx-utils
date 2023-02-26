package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SemanticIdentifier(
		terms = {"licht", "leucht", "beleuchtung", "lamp", "spot",
				"strahl"},
		prefixes = {"L_"},
		description = {"[Licht]"}
)
@Getter
@ToString
@SuperBuilder
public class Light {
	private final String name;
	private final GroupAddress primarySwitchGroupAddress;
	private final GroupAddress statusGroupAddress;
}
