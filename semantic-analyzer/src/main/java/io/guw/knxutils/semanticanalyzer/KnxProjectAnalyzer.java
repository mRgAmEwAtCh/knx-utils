package io.guw.knxutils.semanticanalyzer;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import io.guw.knxutils.semanticanalyzer.analyzer.LightsAnalyzer;
import io.guw.knxutils.semanticanalyzer.analyzer.PowerOutletAnalyzer;
import io.guw.knxutils.semanticanalyzer.analyzer.ShutterAnalyzer;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.GenericCharacteristics;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.LightCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.DimmableLight;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Light;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.KnxProjectFile;

public class KnxProjectAnalyzer {

	private static final Logger LOG = LoggerFactory.getLogger(KnxProjectAnalyzer.class);

	private final KnxProjectFile knxProjectFile;
	private final KnxProjectCharacteristics characteristics = new GenericCharacteristics();
	@Getter
	private LightsAnalyzer lightsAnalyzer;
	@Getter
	private ShutterAnalyzer shutterAnalyzer;
	@Getter
	private PowerOutletAnalyzer powerOutletAnalyzer;

	public KnxProjectAnalyzer(KnxProjectFile knxProjectFile) {
		this.knxProjectFile = knxProjectFile;
	}

	public void analyze() {
		List<GroupAddress> groupAddresses = knxProjectFile.getGroupAddresses();
		if (groupAddresses.isEmpty()) {
			throw new IllegalStateException("The project does not contain any Group Address.");
		}

		// fill in missing information
		groupAddresses.forEach(characteristics::fillInMissingInformation);

		// sanity check
		if (((float) characteristics.getWarnings() / (float) groupAddresses.size()) > 0.10F) {
			LOG.warn("The project data generated a lot of warnings. Please consider improving the ETS data.");
		}

		// find lights
		lightsAnalyzer = new LightsAnalyzer(groupAddresses);
		lightsAnalyzer.analyze();

		// find shutters
		shutterAnalyzer = new ShutterAnalyzer(groupAddresses);
		shutterAnalyzer.analyze();

		// find shutters
		powerOutletAnalyzer = new PowerOutletAnalyzer(groupAddresses);
		powerOutletAnalyzer.analyze();
	}


}
