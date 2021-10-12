package org.molgenis.vkgl.clinvar.model;

public interface ClinVarLine {
  public String getScv();
  public String getVariantDescription();
  public String getGene();
  public String getClinicalSignificance();
}
