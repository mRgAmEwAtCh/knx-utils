package io.guw.knxutils.semanticanalyzer.semanticmodel;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxObject;
import lombok.Data;

@KnxIdentifier(
		terms = {"licht", "leucht", "beleuchtung", "lamp", "spot",
				"strahl"},
		prefixes = {"L_"},
		description = {"[Licht]"}
)
@Data
public class Light extends KnxObject {
	private final String name;
	private final GroupAddress primarySwitchGroupAddress;
	private final GroupAddress statusGroupAddress;
	

}
