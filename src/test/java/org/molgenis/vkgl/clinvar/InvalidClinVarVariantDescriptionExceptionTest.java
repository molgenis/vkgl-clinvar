package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidClinVarVariantDescriptionExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unable to parse ClinVar variant description: 'non parsable xml'.",
        new InvalidClinVarVariantDescriptionException("non parsable xml").getMessage());
  }
}
