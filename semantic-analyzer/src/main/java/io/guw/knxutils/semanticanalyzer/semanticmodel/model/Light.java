package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.SemanticIdentifier;
import lombok.Getter;
import lombok.NonNull;
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
public class Light implements Thing{
	@NonNull
	private final String name;
	@NonNull
	private final GroupAddress primarySwitchGroupAddress;
	@NonNull
	private final GroupAddress statusGroupAddress;
	//public static boolean checkType(GroupAddress ga, GenericCharacteristics.GroupAddressDocument doc){return false;}
}
