package org.molgenis.vkgl.clinvar;

public class DuplicateAccessionException extends RuntimeException {

  private static final String MESSAGE =
      "ClinVar Accession '%s' was seen more than once, this indicates a problem with your mapping files.";

  public DuplicateAccessionException(String accession) {
    super(String.format(MESSAGE,accession));
  }
}
