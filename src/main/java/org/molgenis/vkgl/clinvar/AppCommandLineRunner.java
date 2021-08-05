package org.molgenis.vkgl.clinvar;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_CLINVAR_REPORT;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_DEBUG;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_DUPLICATE_MODE;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_MAPPINGS;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_OUTPUT_DIR;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_RELEASE_NAME;
import static org.molgenis.vkgl.clinvar.AppCommandLineOptions.OPT_SINGLE_MODE;

import ch.qos.logback.classic.Level;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppCommandLineRunner.class);

  private static final int STATUS_MISC_ERROR = 1;
  private static final int STATUS_COMMAND_LINE_USAGE_ERROR = 64;

  private final String appName;
  private final String appVersion;
  private final CommandLineParser commandLineParser;
  private final SubmissionService submissionService;

  AppCommandLineRunner(
      @Value("${app.name}") String appName,
      @Value("${app.version}") String appVersion,
      SubmissionService submissionService) {
    this.appName = requireNonNull(appName);
    this.appVersion = requireNonNull(appVersion);
    this.commandLineParser = new DefaultParser();
    this.submissionService = requireNonNull(submissionService);
  }

  @Override
  public void run(String... args) {
    if (args.length == 1
        && (args[0].equals("-" + AppCommandLineOptions.OPT_VERSION)
        || args[0].equals("--" + AppCommandLineOptions.OPT_VERSION_LONG))) {
      LOGGER.info("{} {}", appName, appVersion);
      return;
    }

    for (String arg : args) {
      if (arg.equals('-' + OPT_DEBUG) || arg.equals('-' + AppCommandLineOptions.OPT_DEBUG_LONG)) {
        Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (!(rootLogger instanceof ch.qos.logback.classic.Logger)) {
          throw new ClassCastException("Expected root logger to be a logback logger");
        }
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.DEBUG);
        break;
      }
    }

    CommandLine commandLine = getCommandLine(args);
    AppCommandLineOptions.validateCommandLine(commandLine);
    Settings settings = mapSettings(commandLine);
    try {
      submissionService.createClinVarSubmission(settings);
    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      System.exit(STATUS_MISC_ERROR);
    }
  }

  private Settings mapSettings(CommandLine commandLine) {
    String inputPathValue = commandLine.getOptionValue(OPT_INPUT);
    Path inputPath = Path.of(inputPathValue);
    String mappingPathValue = commandLine.getOptionValue(OPT_MAPPINGS);
    List<Path> mappings = mapMappings(mappingPathValue);
    Map<Lab,Path> clinVarMappings = getClinVarMappings(commandLine);
    Path outputPath = Path.of(commandLine.getOptionValue(OPT_OUTPUT_DIR));
    String release = commandLine.getOptionValue(OPT_RELEASE_NAME);
    boolean overwriteOutput = commandLine.hasOption(OPT_FORCE);

    boolean debugMode = commandLine.hasOption(OPT_DEBUG);
    boolean isIncludeSingleLab = commandLine.hasOption(OPT_SINGLE_MODE);
    boolean isDeleteDuplicates = commandLine.hasOption(OPT_DUPLICATE_MODE);

    return Settings.builder()
        .input(inputPath)
        .includeSingleLab(isIncludeSingleLab)
        .deleteDuplicates(isDeleteDuplicates)
        .mappings(mappings)
        .clinVarMapping(clinVarMappings)
        .outputDir(outputPath)
        .overwrite(overwriteOutput)
        .release(release)
        .debug(debugMode)
        .build();
  }

  private Map<Lab, Path> getClinVarMappings(CommandLine commandLine) {
    HashMap<Lab, Path> result = new HashMap<>();
    if (commandLine.hasOption(OPT_CLINVAR_REPORT)) {
      String clinVarMappingPathValue = commandLine.getOptionValue(OPT_CLINVAR_REPORT);
      String[] clinVarMappingPerLab = clinVarMappingPathValue.split(",");
      for (String singleLabArgument : clinVarMappingPerLab) {
        String[] split = singleLabArgument.split("=");
        if (split.length == 2) {
          String labName = split[0];
          Path path = Path.of(split[1]);
          result.put(Lab.valueOf(labName), path);
        } else {
          throw new InvalidClinVarArgumentException(singleLabArgument);
        }
      }
    }
    return result;
  }

  private List<Path> mapMappings(String mappingPathValue) {
    List<Path> mappings = new ArrayList<>();
    for(String mapping : mappingPathValue.split(",")){
      mappings.add(Path.of(mapping));
    }
    return mappings;
  }

  private CommandLine getCommandLine(String[] args) {
    CommandLine commandLine = null;
    try {
      commandLine = commandLineParser.parse(AppCommandLineOptions.getAppOptions(), args);
    } catch (ParseException e) {
      logException(e);
      System.exit(STATUS_COMMAND_LINE_USAGE_ERROR);
    }
    return commandLine;
  }

  @SuppressWarnings("java:S106")
  private void logException(ParseException e) {
    LOGGER.error(e.getLocalizedMessage(), e);

    // following information is only logged to system out
    System.out.println();
    HelpFormatter formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    String cmdLineSyntax = "java -jar " + appName + ".jar";
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppOptions(), true);
    System.out.println();
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppVersionOptions(), true);
  }
}
