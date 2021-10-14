package org.molgenis.vkgl.clinvar;

public class HeaderNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Expected headerline not found in ClinVar file '%s'.";

  public HeaderNotFoundException(String filename) {
    super(String.format(MESSAGE, filename));
  }
}
