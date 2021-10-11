package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeaderNotFoundExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Expected headerline not found in ClinVar file 'myfileName.xyz'.",
        new HeaderNotFoundException("myfileName.xyz").getMessage());
  }
}