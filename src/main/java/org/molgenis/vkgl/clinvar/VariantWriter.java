package org.molgenis.vkgl.clinvar;

import static java.util.Objects.requireNonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.ConsensusLine;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.Settings;
import org.molgenis.vkgl.clinvar.model.SubmissionLine;

public class VariantWriter {

  public static final String VARIANT_FORMAT =
      "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n";
  public static final String VARIANT_SHEET = "Variant.tsv";

  private static final String CHROMOSOME = "Chromosome";
  private static final String START = "Start";
  private static final String STOP = "Stop";
  private static final String REF = "Reference allele";
  private static final String ALT = "Alternate allele";
  public static final String PREFERRED_CONDITION_NAME = "Preferred condition name";
  public static final String CLINICAL_SIGNIFICANCE = "Clinical significance";
  public static final String DATE_LAST_EVALUATED = "Date last evaluated";
  public static final String GENE_SYMBOL = "Gene symbol";
  public static final String CLIN_VAR_ACCESSION = "ClinVarAccession";
  private static final String NOVEL_OR_UPDATE = "Novel or Update";
  private static final String ALLELE_ORIGIN = "Allele origin";
  private static final String AFFECTED_STATUS = "Affected status";
  private static final String COLLECTION_METHOD = "Collection method";

  public static final String NOT_PROVIDED = "not provided";
  public static final String NOT_SPECIFIED = "not specified";
  public static final String AFFECTED_STATUS_VALUE = "yes";
  public static final String COLLECTION_METHOD_VALUE = "clinical testing";
  public static final String ALLELE_ORIGIN_VALUE = "germline";

  private static final String HEADER =
      String.format(
          VARIANT_FORMAT,
          CHROMOSOME,
          START,
          STOP,
          REF,
          ALT,
          PREFERRED_CONDITION_NAME,
          CLINICAL_SIGNIFICANCE,
          DATE_LAST_EVALUATED,
          GENE_SYMBOL,
          CLIN_VAR_ACCESSION,
          NOVEL_OR_UPDATE,
          ALLELE_ORIGIN,
          AFFECTED_STATUS,
          COLLECTION_METHOD);
  public static final String UPDATE = "update";
  public static final String NOVEL = "novel";

  private final Path outputDir;
  private final String release;
  private final GenesFilter genesFilter;

  public VariantWriter(Settings settings) {
    requireNonNull(settings);
    this.outputDir = settings.getOutputDir();
    this.release = settings.getRelease();
    this.genesFilter = new GenesFilter(settings);
  }

  public void write(Collection<SubmissionLine> variants, Lab lab) {
    try (FileOutputStream fileOutputStream =
        new FileOutputStream(
            Path.of(
                    outputDir.toString(),
                    String.format("%s_%s.%s", lab.name(), release, VARIANT_SHEET))
                .toFile())) {
      fileOutputStream.write(HEADER.getBytes());
      variants.stream()
          .map(variant -> toTsvString(variant))
          .map(String::getBytes)
          .forEach(
              line -> {
                try {
                  fileOutputStream.write(line);
                } catch (IOException ioException) {
                  throw new UncheckedIOException(ioException);
                }
              });
    } catch (IOException ioException) {
      throw new UncheckedIOException(ioException);
    }
  }

  private String toTsvString(SubmissionLine submissionLine) {
    ConsensusLine line = submissionLine.getConsensusLine();
    String accession = "";
    String type = NOVEL;
    if (submissionLine.getClinVarAccession() != null) {
      accession = submissionLine.getClinVarAccession();
      type = UPDATE;
    }
    String gene = line.getGene();
    return String.format(
        VARIANT_FORMAT,
        line.getChromosome(),
        line.getStart(),
        line.getStop(),
        line.getRef(),
        line.getAlt(),
        getConditionName(line.getClassification(submissionLine.getLab())),
        line.getClassification(submissionLine.getLab()).getLongName(),
        "",
        genesFilter.filter(gene),
        accession,
        type,
        ALLELE_ORIGIN_VALUE,
        AFFECTED_STATUS_VALUE,
        COLLECTION_METHOD_VALUE);
  }

  private static String getConditionName(Classification classification) {
    String conditionName;
    if (classification.equals(Classification.b)) {
      conditionName = NOT_SPECIFIED;
    } else {
      conditionName = NOT_PROVIDED;
    }
    return conditionName;
  }
}
