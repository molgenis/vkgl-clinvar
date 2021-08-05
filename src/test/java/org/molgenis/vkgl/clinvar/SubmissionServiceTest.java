package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.ClinVarLine;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

  @Mock SubmissionDecorator submissionDecorator;

  @Mock ClinVarMapping clinVarMapping;
  private SubmissionService service;

  @BeforeEach
  private void setUp(){
    service = new SubmissionService(submissionDecorator, clinVarMapping);
  }

  @Test
  void processClinVar() {
    ClinVarLine clinVarLine =
        ClinVarLine.builder()
            .clinicalSignificance("Benign")
            .gene("Gene")
            .SCV("SCV123")
            .variantDescription(
                "<SequenceLocation Assembly=\"GRCh37\" Chr=\"9\" alternateAllele=\"G\" referenceAllele=\"C\" start=\"1\" stop=\"1\" variantLength=\"1\"/>")
            .build();
    service.processClinVar(clinVarLine, Lab.amc);

    MappingLine expected =
        MappingLine.builder()
            .chromosome("9")
            .start(1)
            .stop(1)
            .ref("C")
            .alt("G")
            .gene("Gene")
            .clinVarAccession("SCV123")
            .classification(Classification.b)
            .lab("amc")
            .build();

    verify(clinVarMapping).addMapping(expected);
  }

  @Test
  void processClinVarException() {
    ClinVarLine clinVarLine =
        ClinVarLine.builder()
            .clinicalSignificance("Benign")
            .gene("Gene")
            .SCV("SCV123")
            .variantDescription(
                "SequenceLocation Assembly=\"GRCh37\" Chr=\"9\" alternateAllele=\"G\" referenceAllele=\"C\" start=\"1\" stop=\"1\" variantLength=\"1\"/>")
            .build();
    assertThrows(
        InvalidClinVarVariantDescriptionException.class,
        () -> {
          service.processClinVar(clinVarLine, Lab.amc);
        });
    }
}