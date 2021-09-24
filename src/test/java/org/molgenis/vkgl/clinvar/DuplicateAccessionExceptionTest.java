package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DuplicateAccessionExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "ClinVar Accession 'SVC1234567' was seen more than once, this indicates a problem with your mapping files.",
        new DuplicateAccessionException("SVC1234567").getMessage());
  }
}
