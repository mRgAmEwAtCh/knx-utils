package io.guw.knxutils.semanticanalyzer.semanticmodel;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.util.KnxIdentifier;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@KnxIdentifier(
		terms = {"licht", "leucht", "beleuchtung", "lamp", "spot",
				"strahl"},
		prefixes = {"LD_", "LDA_"}
)
@Getter
@ToString(callSuper = true)
@SuperBuilder
public class DimmableLight extends Light {
	private final GroupAddress dimGa;
	private final GroupAddress brightnessGa;
	private final GroupAddress brightnessStatusGa;
}
