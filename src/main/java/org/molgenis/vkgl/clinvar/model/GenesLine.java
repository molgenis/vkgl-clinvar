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
  public class GenesLine{

    @CsvBindByName(column = "HGNC ID")
    String id;

    @CsvBindByName(column = "Status")
    String status;

    @CsvBindByName(column = "Approved symbol", required = true)
    String symbol;

    @CsvBindByName(column = "Approved name")
    String name;

}
