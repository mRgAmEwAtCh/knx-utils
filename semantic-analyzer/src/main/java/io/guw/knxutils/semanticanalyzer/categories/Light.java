package io.guw.knxutils.semanticanalyzer.categories;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.categories.util.KnxIdentifier;
import io.guw.knxutils.semanticanalyzer.categories.util.KnxObject;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

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
