package io.guw.knxutils.semanticanalyzer.categories;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.categories.util.KnxIdentifier;
import lombok.Getter;
import lombok.ToString;

@KnxIdentifier(
		terms = {"licht", "leucht", "beleuchtung", "lamp", "spot",
				"strahl"},
		prefixes = {"LD_", "LDA_"}
)
@ToString(callSuper = true)
public class DimmableLight extends Light {
	@Getter
	private final GroupAddress dimGa;
	@Getter
	private final GroupAddress brightnessGa;
	@Getter
	private final GroupAddress brightnessStatusGa;

	public DimmableLight(String name, GroupAddress primaryGa, GroupAddress statusGa, GroupAddress dimGa,
						 GroupAddress brightnessGa, GroupAddress brightnessStatusGa) {
		super(name, primaryGa, statusGa);
		this.dimGa = dimGa;
		this.brightnessGa = brightnessGa;
		this.brightnessStatusGa = brightnessStatusGa;
	}
}
