package org.molgenis.vkgl.clinvar;

public class InvalidClinVarAccessionException extends RuntimeException {

  private static final String MESSAGE = "Invalid ClinVarAccession: '%s'.";

  public InvalidClinVarAccessionException(String argument) {
    super(String.format(MESSAGE,argument));
  }
}
