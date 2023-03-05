package io.guw.knxutils.cli;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import io.guw.knxutils.knxprojectparser.KnxProjectFile;
import io.guw.knxutils.openhabtemplateprocessor.OpenhabTemplateProcessor;
import io.guw.knxutils.semanticanalyzer.KnxProjectAnalyzer;

import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Thing;
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
		final List<Thing> model = analyzer.analyze();

		model.forEach(thing -> log.info(thing.toString()));

		final OpenhabTemplateProcessor openhabTemplateProcessor = new OpenhabTemplateProcessor(model);

		openhabTemplateProcessor.generateModel();

		return null;
	}

}
