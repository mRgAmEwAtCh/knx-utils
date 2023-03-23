package io.guw.knxutils.semanticanalyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.KnxProjectFile;
import io.guw.knxutils.semanticanalyzer.analyzer.HeatingAnalyzer;
import io.guw.knxutils.semanticanalyzer.analyzer.LightsAnalyzer;
import io.guw.knxutils.semanticanalyzer.analyzer.PowerOutletAnalyzer;
import io.guw.knxutils.semanticanalyzer.analyzer.ShutterAnalyzer;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.GenericCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KnxProjectAnalyzer {

	private static final Logger LOG = LoggerFactory.getLogger(KnxProjectAnalyzer.class);

	private final KnxProjectFile knxProjectFile;
	private final KnxProjectCharacteristics characteristics = new GenericCharacteristics();
	public KnxProjectAnalyzer(KnxProjectFile knxProjectFile) {
		this.knxProjectFile = knxProjectFile;
	}

	public List<Thing> analyze() {
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
		LightsAnalyzer lightsAnalyzer = new LightsAnalyzer(groupAddresses);
		lightsAnalyzer.analyze();

		// find shutters
		ShutterAnalyzer shutterAnalyzer = new ShutterAnalyzer(groupAddresses);
		shutterAnalyzer.analyze();

		// find shutters
		PowerOutletAnalyzer powerOutletAnalyzer = new PowerOutletAnalyzer(groupAddresses);
		powerOutletAnalyzer.analyze();

		HeatingAnalyzer heatingAnalyzer = new HeatingAnalyzer((groupAddresses));
		heatingAnalyzer.analyze();

		return Stream.of(
				lightsAnalyzer.getLights(),
				shutterAnalyzer.getShutters(),
				powerOutletAnalyzer.getPowerOutlets(),
				heatingAnalyzer.getHeatings()
		).flatMap(Collection::parallelStream).collect(Collectors.toList());
	}

}
