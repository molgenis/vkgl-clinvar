package org.molgenis.vkgl.clinvar;

import static java.lang.String.format;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class AppCommandLineOptions {

  static final String OPT_INPUT = "i";
  static final String OPT_INPUT_LONG = "input";
  static final String OPT_MAPPINGS = "m";
  static final String OPT_MAPPINGS_LONG = "mappings";
  static final String OPT_CLINVAR_REPORT = "c";
  static final String OPT_CLINVAR_REPORT_LONG = "clinVar";
  static final String OPT_OUTPUT_DIR = "o";
  static final String OPT_OUTPUT_DIR_LONG = "output";
  static final String OPT_RELEASE_NAME = "r";
  static final String OPT_RELEASE_NAME_LONG = "release";
  static final String OPT_DUPLICATE_MODE = "dd";
  static final String OPT_DUPLICATE_LONG = "delete_duplicated";
  static final String OPT_SINGLE_MODE = "s";
  static final String OPT_SINGLE_MODE_LONG = "include_single_lab";
  static final String OPT_FORCE = "f";
  static final String OPT_FORCE_LONG = "force";
  static final String OPT_DEBUG = "d";
  static final String OPT_DEBUG_LONG = "debug";
  static final String OPT_VERSION = "v";
  static final String OPT_VERSION_LONG = "version";
  private static final Options APP_OPTIONS;
  private static final Options APP_VERSION_OPTIONS;
  private static final String TSV = ".tsv";

  static {
    Options appOptions = new Options();
    appOptions.addOption(
        Option.builder(OPT_INPUT)
            .hasArg(true)
            .required()
            .longOpt(OPT_INPUT_LONG)
            .desc("VKGL consensus file (tsv).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_MAPPINGS)
            .hasArg(true)
            .longOpt(OPT_MAPPINGS_LONG)
            .desc("Mapping log files from previous run with this tool.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_CLINVAR_REPORT)
            .hasArg(true)
            .longOpt(OPT_CLINVAR_REPORT_LONG)
            .desc("ClinVar Submission reports from previous submission, format: lab1=report path,lab2=report path")
            .build());
    appOptions.addOption(
        Option.builder(OPT_OUTPUT_DIR)
            .hasArg(true)
            .required()
            .longOpt(OPT_OUTPUT_DIR_LONG)
            .desc("Directory to write output to.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_RELEASE_NAME)
            .hasArg(true)
            .required()
            .longOpt(OPT_RELEASE_NAME_LONG)
            .desc("Release name to use in ClinVar file names.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_DUPLICATE_MODE)
            .longOpt(OPT_DUPLICATE_LONG)
            .desc("Flag to indicate is existing duplicated should be deleted.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_SINGLE_MODE)
            .longOpt(OPT_SINGLE_MODE_LONG)
            .desc("Flag to indicate that single lab submissions should also be submitted.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_FORCE)
            .longOpt(OPT_FORCE_LONG)
            .desc("Override the output files if output directory already exists.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_DEBUG)
            .longOpt(OPT_DEBUG_LONG)
            .desc("Enable debug mode (additional logging).")
            .build());
    APP_OPTIONS = appOptions;
    Options appVersionOptions = new Options();
    appVersionOptions.addOption(
        Option.builder(OPT_VERSION)
            .required()
            .longOpt(OPT_VERSION_LONG)
            .desc("Print version.")
            .build());
    APP_VERSION_OPTIONS = appVersionOptions;
  }

  private AppCommandLineOptions() {
  }

  static Options getAppOptions() {
    return APP_OPTIONS;
  }

  static Options getAppVersionOptions() {
    return APP_VERSION_OPTIONS;
  }

  static void validateCommandLine(CommandLine commandLine) {
    validateInput(commandLine);
    validateMapping(commandLine);
    validateClinVar(commandLine);
    validateOutput(commandLine);
  }

  private static void validateInput(CommandLine commandLine) {
    Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
    validatePath(inputPath, TSV);
  }

  private static void validateMapping(CommandLine commandLine) {
    String mappingPathValue = commandLine.getOptionValue(OPT_MAPPINGS);
    for(String mapping : mappingPathValue.split(",")){
      validatePath(Path.of(mapping), TSV);
    }
  }

  private static void validateClinVar(CommandLine commandLine) {
    if (commandLine.hasOption(OPT_CLINVAR_REPORT)) {
      String mappingPathValue = commandLine.getOptionValue(OPT_CLINVAR_REPORT);
      for (String mapping : mappingPathValue.split(",")) {
        String[] split = mapping.split("=");
        if (split.length != 2) {
          throw new InvalidClinVarArgumentException(mapping);
        }
        validatePath(Path.of(split[1]), ".txt");
      }
    }
  }

  private static void validatePath(Path inputPath, String extension) {
    if (!Files.exists(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' does not exist.", inputPath.toString()));
    }
    if (Files.isDirectory(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' is a directory.", inputPath.toString()));
    }
    if (!Files.isReadable(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' is not readable.", inputPath.toString()));
    }
    String inputPathStr = inputPath.toString();
    if (!inputPathStr.endsWith(".vcf") && !inputPathStr.endsWith(extension)) {
      throw new IllegalArgumentException(
          format("Input file '%s' is not a %s file.", inputPathStr, extension));
    }
  }

  private static void validateOutput(CommandLine commandLine) {
    if (!commandLine.hasOption(OPT_OUTPUT_DIR)) {
      return;
    }

    Path outputPath = Path.of(commandLine.getOptionValue(OPT_OUTPUT_DIR));

    if (!commandLine.hasOption(OPT_FORCE) && !Files.isDirectory(outputPath)) {
      throw new IllegalArgumentException(
          format("Output '%s' is not a directory.", outputPath.toString()));
    }

    if (outputPath.toFile().listFiles().length > 0 && !commandLine.hasOption(OPT_FORCE)) {
      throw new IllegalArgumentException(
          format(
              "Output directory '%s' is not empty, use '-f' to overwrite existing files.",
              outputPath.toString()));
    }
  }
}
