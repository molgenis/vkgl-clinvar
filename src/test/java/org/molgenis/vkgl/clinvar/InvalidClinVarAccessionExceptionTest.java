package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidClinVarAccessionExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Invalid ClinVarAccession: 'SVC1234567.1.2'.",
        new InvalidClinVarAccessionException("SVC1234567.1.2").getMessage());
  }
}
