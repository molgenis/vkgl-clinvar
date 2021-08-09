package org.molgenis.vkgl.clinvar.fix042021;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.molgenis.vkgl.clinvar.CsvReader;
import org.molgenis.vkgl.clinvar.InvalidClinVarVariantDescriptionException;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.ClinVarLine;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.SubmittedLine;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class releaseToUpdateCoverter {

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

  public static void main(String args[]) {
    CsvReader<SubmittedLine> lineReader = new CsvReader<>();

    Path inputPath = Path.of(args[0]);
    Path reportPath = Path.of(args[1]);

    Map<Location, MappingLine> clinVarMapping = new HashMap<>();
    readClinVarReport(reportPath, clinVarMapping);
    List<SubmittedLine> submittedLines = lineReader.read(inputPath, SubmittedLine.class).stream().map(line -> mapLine(line, clinVarMapping)).collect(
        Collectors.toList());
    write(inputPath, submittedLines);
  }

  private static SubmittedLine mapLine(SubmittedLine line, Map<Location, MappingLine> clinVarMapping) {
    Location location =
        Location.builder()
            .chromosome(line.getChromosome())
            .start(line.getStart())
            .stop(line.getStop())
            .ref(line.getRef())
            .alt(line.getAlt())
            .gene(line.getGeneSymbol())
            .build();
    MappingLine mappingLine = clinVarMapping.get(location);
    if(mappingLine == null){
      throw new RuntimeException("ERROR");
    }
    line.setNoverOrUpdate("update");
    line.setClinVarAccession(mappingLine.getClinVarAccession());
    return line;
  }

  public static void write(Path inputFile, Collection<SubmittedLine> variants) {
    try (FileOutputStream fileOutputStream =
        new FileOutputStream(
            Path.of(
                inputFile.getParent().toString(),
                String.format("%s_%s.%s", "umcg", "202104Update", VARIANT_SHEET))
                .toFile())) {
      fileOutputStream.write(HEADER.getBytes());
      variants.stream()
          .map(line -> toTsvString(line))
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

  private static String toTsvString(SubmittedLine line) {
    return String.format(
        VARIANT_FORMAT,
        line.getChromosome(),
        line.getStart(),
        line.getStop(),
        line.getRef(),
        line.getAlt(),
        line.getPrefferedConditionName(),
        line.getClinicalSignificance(),
        line.getDateLastEvaluated(),
        line.getGeneSymbol(),
        line.getClinVarAccession(),
        line.getNoverOrUpdate(),
        line.getAlleleOrigin(),
        line.getAffectedStatus(),
        line.getCollectionMethod());
  }

  private static void readClinVarReport(Path path, Map<Location, MappingLine> clinVarMapping) {
    try (Reader reader = Files.newBufferedReader(path, UTF_8)) {
      CsvToBean<ClinVarLine> csvToBean =
          new CsvToBeanBuilder<ClinVarLine>(reader)
              .withSkipLines(23)
              .withSeparator('\t')
              .withType(ClinVarLine.class)
              .build();
      csvToBean.forEach(line -> processClinVar(line, Lab.umcg, clinVarMapping));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected static void processClinVar(
      ClinVarLine line, Lab lab, Map<Location, MappingLine> clinVarMapping) {
    Element location = parseXml(line.getVariantDescription());
    String chromosome = location.getAttribute("Chr");
    Integer start = Integer.parseInt(location.getAttribute("start"));
    Integer stop = Integer.parseInt(location.getAttribute("stop"));
    String ref = location.getAttribute("referenceAllele");
    String alt = location.getAttribute("alternateAllele");
    Classification classification = Classification.of(line.getClinicalSignificance());
    MappingLine mappingLine =
        MappingLine.builder()
            .chromosome(chromosome)
            .start(start)
            .stop(stop)
            .ref(ref)
            .alt(alt)
            .gene(line.getGene())
            .clinVarAccession(line.getScv())
            .classification(classification)
            .lab(lab.name())
            .build();
    clinVarMapping.put(
        Location.builder()
            .chromosome(mappingLine.getChromosome())
            .start(mappingLine.getStart())
            .stop(mappingLine.getStop())
            .ref(mappingLine.getRef())
            .alt(mappingLine.getAlt())
            .gene(mappingLine.getGene())
            .build(),
        mappingLine);
  }

  private static Element parseXml(String xml) {
    Element result;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(xml));
      result = builder.parse(is).getDocumentElement();
    } catch (Exception e) {
      throw new InvalidClinVarVariantDescriptionException(xml);
    }
    return result;
  }
}
