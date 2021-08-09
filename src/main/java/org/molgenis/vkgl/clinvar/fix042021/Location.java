package org.molgenis.vkgl.clinvar.fix042021;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
  String chromosome;
  Integer start;
  Integer stop;
  String ref;
  String alt;
  String gene;
}
