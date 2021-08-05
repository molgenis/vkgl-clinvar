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
public class ClinVarLine {
  @CsvBindByName(column = "SCV", required = true)
  String SCV;

  @CsvBindByName(column = "Your_variant_description", required = true)
  String variantDescription;

  @CsvBindByName(column = "Submitted_gene", required = true)
  String gene;

  @CsvBindByName(column = "Clinical_significance", required = true)
  String clinicalSignificance;
}
