package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DuplicateSubmittedAccessionExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Multiple ClinVar accessions found for the same duplicate variant: 'test1', 'test2'.",
        new DuplicateSubmittedAccessionException("test1", "test2").getMessage());
  }
}
