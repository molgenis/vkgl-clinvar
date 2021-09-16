package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.molgenis.vkgl.clinvar.model.Lab.umcg;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vkgl.clinvar.model.Classification;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.VariantId;

class ClinVarMappingTest {

  ClinVarMapping clinVarMapping;

  MappingLine mappingLine1 =
      MappingLine.builder()
          .lab("umcg")
          .chromosome("1")
          .start(2)
          .stop(3)
          .ref("T")
          .alt("A")
          .gene("GENE2")
          .clinVarAccession("234")
          .classification(Classification.b)
          .build();
  MappingLine mappingLine2 =
      MappingLine.builder()
          .lab("umcg")
          .chromosome("1")
          .start(2)
          .stop(4)
          .ref("T")
          .alt("A")
          .gene("GENE3")
          .clinVarAccession("345")
          .classification(Classification.b)
          .build();

  @BeforeEach
  void setUp() {
    this.clinVarMapping = new ClinVarMapping();
  }

  @Test
  void addMapping() {
    clinVarMapping.addMapping(mappingLine1);
    assertEquals(mappingLine1, clinVarMapping.getMapping(umcg, new VariantId(mappingLine1)));
  }

  @Test
  void addMappingDuplicate() {
    clinVarMapping.addMapping(mappingLine1);
    assertThrows(DuplicateAccessionException.class, () -> clinVarMapping.addMapping(mappingLine1));
  }

  @Test
  void getAllMappingLines() {
    clinVarMapping.addMapping(mappingLine1);
    clinVarMapping.addMapping(mappingLine2);

    assertEquals(
        Set.of(mappingLine1, mappingLine2),
        clinVarMapping.getAllMappingLines(umcg).stream().collect(Collectors.toSet()));
  }

  @Test
  void containsKey() {
    clinVarMapping.addMapping(mappingLine1);
    assertEquals(true, clinVarMapping.containsKey(umcg, new VariantId(mappingLine1)));
  }
}
