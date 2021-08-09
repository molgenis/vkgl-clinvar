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
public class SubmittedLine {
  @CsvBindByName(column = "Chromosome", required = true)
  String chromosome;
  @CsvBindByName(column = "Start", required = true)
  Integer start;
  @CsvBindByName(column = "Stop", required = true)
  Integer stop;
  @CsvBindByName(column = "Reference allele", required = true)
  String ref;
  @CsvBindByName(column = "Alternate allele", required = true)
  String alt;
  @CsvBindByName(column = "Preferred condition name", required = true)
  String prefferedConditionName;
  @CsvBindByName(column = "Clinical significance", required = true)
  String clinicalSignificance;
  @CsvBindByName(column = "Date last evaluated", required = false)
  String dateLastEvaluated;
  @CsvBindByName(column = "Gene symbol", required = true) 
  String geneSymbol;
  @CsvBindByName(column = "ClinVarAccession", required = false)
  String clinVarAccession;
  @CsvBindByName(column = "Novel or Update", required = true)
  String noverOrUpdate;
  @CsvBindByName(column = "Allele origin", required = true)
  String alleleOrigin;
  @CsvBindByName(column = "Affected status", required = true)
  String affectedStatus;
  @CsvBindByName(column = "Collection method", required = true)
  String collectionMethod;
}
