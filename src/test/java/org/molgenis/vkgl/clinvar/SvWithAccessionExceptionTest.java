package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SvWithAccessionExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported structural variants should not have an existing ClinVar Accession.",
        new SvWithAccessionException().getMessage());
  }
}
