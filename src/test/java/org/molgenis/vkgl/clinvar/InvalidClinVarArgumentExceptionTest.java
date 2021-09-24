package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidClinVarArgumentExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Expecting lab/path key value pairs (lab1name=path1,lab2name=path2), got 'filepath' instead.",
        new InvalidClinVarArgumentException("filepath").getMessage());
  }
}
