package org.molgenis.vkgl.clinvar;

public class InvalidClinVarArgumentException extends RuntimeException {

  private static final String MESSAGE = "Expecting lab/path key value pairs (lab1name=path1,lab2name=path2), got '%s' instead.";

  public InvalidClinVarArgumentException(String argument) {
    super(String.format(MESSAGE,argument));
  }
}
