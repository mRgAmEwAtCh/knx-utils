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
		prefixes = {"LD_", "LDA_"},
		description = {"[Dimmer]"}
)
@Getter
@ToString(callSuper = true)
@SuperBuilder
public class DimmableLight extends Light {
	@NonNull
	private final GroupAddress dimGa;
	@NonNull
	private final GroupAddress brightnessGa;
	@NonNull
	private final GroupAddress brightnessStatusGa;
}
