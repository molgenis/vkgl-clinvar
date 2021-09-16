package org.molgenis.vkgl.clinvar;

public class DuplicateSubmittedAccessionException extends RuntimeException {

  private static final String MESSAGE = "Multiple ClinVar accessions found for the same duplicate variant: '%s', '%s'.";

  public DuplicateSubmittedAccessionException(String clinVarAccession1, String clinVarAccession2) {
    super(String.format(MESSAGE,clinVarAccession1, clinVarAccession2));
  }
}
