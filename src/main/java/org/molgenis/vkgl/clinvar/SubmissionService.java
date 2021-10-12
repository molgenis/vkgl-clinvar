package org.molgenis.vkgl.clinvar;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vkgl.clinvar.ClinVarHeaderEnum.NEW;
import static org.molgenis.vkgl.clinvar.ClinVarHeaderEnum.OLD;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.ClinVarHeaderMeta;
import org.molgenis.vkgl.clinvar.model.ClinVarLine;
import org.molgenis.vkgl.clinvar.model.ClinVarLineNew;
import org.molgenis.vkgl.clinvar.model.ClinVarLineOld;
import org.molgenis.vkgl.clinvar.model.ConsensusLine;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.Settings;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

@Component
public class SubmissionService {

  private static final String CLINVAR_HEADER = "#Your_variant_id\tVariationID";
  private static final String CLINVAR_HEADER_OLD = "#Your_variant_id\tVariantID";
  private SubmissionDecorator submissionDecorator;
  private ClinVarMapping clinVarMapping;

  public SubmissionService(SubmissionDecorator submissionDecorator, ClinVarMapping clinVarMapping) {
    this.submissionDecorator = requireNonNull(submissionDecorator);
    this.clinVarMapping = requireNonNull(clinVarMapping);
  }

  public void createClinVarSubmission(Settings settings) {
    ClinVarWriter writer = new ClinVarWriter(settings);

    CsvReader<ConsensusLine> consensusReader = new CsvReader<>();
    CsvReader<MappingLine> mappingReader = new CsvReader<>();
    for (Path mapping : settings.getMappings()) {
      mappingReader.read(mapping, MappingLine.class).forEach(this::processMapping);
    }
    readClinVarReport(settings);
    consensusReader.read(settings.getInput(), ConsensusLine.class).forEach(this::processConsensus);
    submissionDecorator.variantTraige(settings.isIncludeSingleLab());

    writer.write(submissionDecorator);
  }

  private void readClinVarReport(Settings settings) {
    if (settings.getClinVarMapping() != null) {
      for (Entry<Lab, Path> entry : settings.getClinVarMapping().entrySet()) {
        ClinVarHeaderMeta headerMeta = getHeaderMeta(entry.getValue().toFile());
        int nrOfLinesToSkip = headerMeta.getNrOfHeaders() - 1;
        try (Reader reader = Files.newBufferedReader(entry.getValue(), UTF_8)) {
          CsvToBean<ClinVarLine> csvToBean;
          if (headerMeta.getType() == OLD) {
            csvToBean = getCsvToBean(ClinVarLineOld.class, reader, nrOfLinesToSkip);
          } else {
            csvToBean = getCsvToBean(ClinVarLineNew.class, reader, nrOfLinesToSkip);
          }
          csvToBean.forEach(line -> processClinVar(line, entry.getKey()));
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
  }

  public static <T extends ClinVarLine> CsvToBean<T> getCsvToBean(
      Class T, Reader reader, int nrOfLinesToSkip) {
    return new CsvToBeanBuilder<T>(reader)
        .withSkipLines(nrOfLinesToSkip)
        .withSeparator('\t')
        .withType(T)
        .build();
  }

  private ClinVarHeaderMeta getHeaderMeta(File file) {
    ClinVarHeaderMeta clinVarHeaderMeta = null;
    try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
      String line;
      while ((line = lineNumberReader.readLine()) != null && clinVarHeaderMeta == null) {
        if (line.startsWith(CLINVAR_HEADER)) {
          clinVarHeaderMeta = new ClinVarHeaderMeta(NEW, lineNumberReader.getLineNumber());
        } else if (line.startsWith(CLINVAR_HEADER_OLD)) {
          clinVarHeaderMeta = new ClinVarHeaderMeta(OLD, lineNumberReader.getLineNumber());
        }
      }
    } catch (IOException ioException) {
      throw new UncheckedIOException(ioException);
    }
    if (clinVarHeaderMeta == null) {
      throw new HeaderNotFoundException(file.getName());
    }
    return clinVarHeaderMeta;
  }

  protected void processClinVar(ClinVarLine line, Lab lab) {
    Element location = parseXml(line.getVariantDescription());
    String chromosome = location.getAttribute("Chr");
    Integer start = Integer.parseInt(location.getAttribute("start"));
    Integer stop = Integer.parseInt(location.getAttribute("stop"));
    String ref = location.getAttribute("referenceAllele");
    String alt = location.getAttribute("alternateAllele");
    Classification classification = Classification.of(line.getClinicalSignificance());
    String clinVarAccession = processClinVarAccession(line.getScv());

    MappingLine mappingLine =
        MappingLine.builder()
            .chromosome(chromosome)
            .start(start)
            .stop(stop)
            .ref(ref)
            .alt(alt)
            .gene(line.getGene())
            .clinVarAccession(clinVarAccession)
            .classification(classification)
            .lab(lab.name())
            .build();
    clinVarMapping.addMapping(mappingLine);
  }

  private String processClinVarAccession(String accession) {
    String[] clinvarAccessionSplit = accession.split("\\.");
    String clinVarAccession;
    if (clinvarAccessionSplit.length == 2) {
      clinVarAccession = clinvarAccessionSplit[0];
    } else {
      throw new InvalidClinVarAccessionException(accession);
    }
    return clinVarAccession;
  }

  private Element parseXml(String xml) {
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

  private void processMapping(MappingLine line) {
    clinVarMapping.addMapping(line);
  }

  private void processConsensus(ConsensusLine line) {
    for (Lab lab : Lab.values()) {
      if (line.getClassification(lab) != null) {
        submissionDecorator.addConsensusLine(line, lab);
      }
    }
  }
}
