package io.guw.knxutils.semanticanalyzer.semanticmodel;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxThing;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@KnxIdentifier(
		terms = {"licht", "leucht", "beleuchtung", "lamp", "spot",
				"strahl"},
		prefixes = {"L_"},
		description = {"[Licht]"}
)
@Getter
@ToString
@SuperBuilder
public class Light extends KnxThing {
	private final String name;
	private final GroupAddress primarySwitchGroupAddress;
	private final GroupAddress statusGroupAddress;
}
