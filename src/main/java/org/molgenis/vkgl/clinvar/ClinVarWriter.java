package org.molgenis.vkgl.clinvar;

import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.MappingType;
import org.molgenis.vkgl.clinvar.model.Settings;

public class ClinVarWriter {

  private final VariantWriter variantWriter;
  private final DeleteWriter deleteWriter;
  private final LogWriter logWriter;

  public ClinVarWriter(Settings settings) {
    this.variantWriter = new VariantWriter(settings);
    this.deleteWriter = new DeleteWriter(settings);
    this.logWriter = new LogWriter(settings);
  }

  public void write(SubmissionDecorator submissionDecorator) {
    for (Lab lab : Lab.values()) {
      variantWriter.write(submissionDecorator.getUpdated(lab), lab);
      deleteWriter.write(
          submissionDecorator.getDeletedMappings(lab).stream()
              .filter(mappingLine -> mappingLine.getType() != MappingType.DELETE)
              .map(MappingLine::getClinVarAccession)
              .toList(),
          lab);
    }
    logWriter.write(submissionDecorator);
  }
}
