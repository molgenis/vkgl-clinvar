package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.molgenis.vkgl.clinvar.model.Lab.umcg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.ConsensusLine;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.SubmissionLine;
import org.molgenis.vkgl.clinvar.model.Type;

class DuplicateVariantUtilTest {

  //no clinvar
  //single clinvar
  //multi clinvar -> exception
  //single clinvar parameterized test with classification combinations

  MappingLine mappingLine1 =
      MappingLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE")
          .clinVarAccession("123")
          .classification(Classification.b)
          .build();

  MappingLine mappingLine2 =
      MappingLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE")
          .clinVarAccession("234")
          .classification(Classification.b)
          .build();

  ConsensusLine consensusLine1 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE")
          .umcg(Classification.lp)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine1 = new SubmissionLine(consensusLine1, umcg, mappingLine1);

  ConsensusLine consensusLine2 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE2")
          .umcg(Classification.p)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine2 = new SubmissionLine(consensusLine2, umcg, mappingLine1);

  ConsensusLine consensusLine3 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE3")
          .umcg(Classification.b)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine3 = new SubmissionLine(consensusLine3, umcg, mappingLine1);

  ConsensusLine consensusLine4 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE3")
          .umcg(Classification.b)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine4 = new SubmissionLine(consensusLine4, umcg, mappingLine2);

  ConsensusLine consensusLine5 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE1")
          .umcg(Classification.b)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine5 = new SubmissionLine(consensusLine5, umcg, null);

  ConsensusLine consensusLine6 =
      ConsensusLine.builder()
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE2")
          .umcg(Classification.p)
          .nrLabs(1)
          .type(Type.total_agreement)
          .build();
  SubmissionLine submissionLine6 = new SubmissionLine(consensusLine6, umcg, null);

  @Test
  void getMostSevereVariantSingleAccession() {
    Set<SubmissionLine> variants = Set.of(submissionLine1, submissionLine2, submissionLine3);
    DuplicateVariantUtil.getMostSevereVariant(variants);

    assertEquals(submissionLine2, DuplicateVariantUtil.getMostSevereVariant(variants));
  }

  @Test
  void getMostSevereVariantNoAccession() {
    Set<SubmissionLine> variants = Set.of(submissionLine5, submissionLine6);
    assertEquals(submissionLine6, DuplicateVariantUtil.getMostSevereVariant(variants));
  }

  @Test
  void getMostSevereVariantMultiAccession() {
    Set<SubmissionLine> variants = Set.of(submissionLine1, submissionLine2, submissionLine4);
    assertThrows(DuplicateSubmittedAccessionException.class, () ->DuplicateVariantUtil.getMostSevereVariant(variants));
  }

  @ParameterizedTest
  @CsvSource({"lp,b,vus,GENE3","b,lb,vus,GENE2", "lp,p,lp,GENE1", "b,lb,b,GENE1", "p,vus,lp,GENE3", "vus,vus,vus,GENE1"})
  void getMostSevereParamterized(Classification classificationA,
      Classification classificationB,Classification classificationC, String expectedGene) {
    ConsensusLine consensusLineA =
        ConsensusLine.builder()
            .chromosome("1")
            .start(2)
            .stop(3)
            .ref("T")
            .alt("A")
            .gene("GENE3")
            .umcg(classificationA)
            .nrLabs(1)
            .type(Type.total_agreement)
            .build();
    SubmissionLine submissionLineA = new SubmissionLine(consensusLineA, umcg, null);

    ConsensusLine consensusLineB =
        ConsensusLine.builder()
            .chromosome("1")
            .start(2)
            .stop(3)
            .ref("T")
            .alt("A")
            .gene("GENE1")
            .umcg(classificationB)
            .nrLabs(1)
            .type(Type.total_agreement)
            .build();
    SubmissionLine submissionLineB = new SubmissionLine(consensusLineB, umcg, null);

    ConsensusLine consensusLineC =
        ConsensusLine.builder()
            .chromosome("1")
            .start(2)
            .stop(3)
            .ref("T")
            .alt("A")
            .gene("GENE2")
            .umcg(classificationC)
            .nrLabs(1)
            .type(Type.total_agreement)
            .build();
    SubmissionLine submissionLineC = new SubmissionLine(consensusLineC, umcg, null);

    Set<SubmissionLine> variants = Set.of(submissionLineA, submissionLineB, submissionLineC);

    assertEquals(expectedGene, DuplicateVariantUtil.getMostSevereVariant(variants).getConsensusLine().getGene());
  }
}