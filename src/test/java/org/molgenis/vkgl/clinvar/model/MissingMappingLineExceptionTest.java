package org.molgenis.vkgl.clinvar.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingMappingLineExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Cannot set a comment for a submission without an existing ClinVarMapping.",
        new MissingMappingLineException().getMessage());
  }
}