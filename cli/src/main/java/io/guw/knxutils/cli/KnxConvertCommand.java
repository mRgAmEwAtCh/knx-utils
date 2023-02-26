package io.guw.knxutils.cli;

import java.io.File;
import java.util.concurrent.Callable;

import io.guw.knxutils.knxprojectparser.KnxProjectFile;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.GenericCharacteristics;
import io.guw.knxutils.semanticanalyzer.KnxProjectAnalyzer;
import io.guw.knxutils.semanticanalyzer.KnxProjectCharacteristics;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Converts a knxproj file to a set of configuration files.
 */
@Command(name = "knxconvert")
@Slf4j
public class KnxConvertCommand implements Callable<Void> {

	public static void main(String[] args) {
		CommandLine cmd = new CommandLine(new KnxConvertCommand());
		int exitCode = cmd.execute(args);
		System.exit(exitCode);
	}

	@Parameters(index = "0", description = "the .knxproj file to convert", paramLabel = "FILE")
	private File knxProjFile;

	@Override
	public Void call() throws Exception {

		KnxProjectFile knxProjectFile = new KnxProjectFile(knxProjFile);
		knxProjectFile.open();

		KnxProjectAnalyzer analyzer = new KnxProjectAnalyzer(knxProjectFile);
		analyzer.analyze();

		analyzer.getLightsAnalyzer().getLights().forEach(light -> log.info(light.toString()));
		analyzer.getShutterAnalyzer().getShutters().forEach(shutter -> log.info(shutter.toString()));
		analyzer.getPowerOutletAnalyzer().getPowerOutlets().forEach(powerOutlet -> log.info(powerOutlet.toString()));

		return null;
	}

}
