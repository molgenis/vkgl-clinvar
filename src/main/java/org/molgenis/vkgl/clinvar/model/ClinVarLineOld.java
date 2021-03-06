package org.molgenis.vkgl.clinvar.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClinVarLineOld implements ClinVarLine{
  @CsvBindByName(column = "SCV", required = true)
  String scv;

  @CsvBindByName(column = "Your_variant_description", required = true)
  String variantDescription;

  @CsvBindByName(column = "Submitted_gene")
  String gene;

  @CsvBindByName(column = "Clinical_significance", required = true)
  String clinicalSignificance;
}
