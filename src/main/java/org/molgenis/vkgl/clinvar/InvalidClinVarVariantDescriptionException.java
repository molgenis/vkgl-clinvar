package org.molgenis.vkgl.clinvar;

public class InvalidClinVarVariantDescriptionException extends RuntimeException {

  private static final String MESSAGE = "Unable to parse ClinVar variant description: '%s'.";

  public InvalidClinVarVariantDescriptionException(String variantDescription) {
    super(String.format(MESSAGE,variantDescription));
  }
}
