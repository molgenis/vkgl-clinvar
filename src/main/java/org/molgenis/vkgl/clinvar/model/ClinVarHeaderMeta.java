package org.molgenis.vkgl.clinvar.model;

import lombok.Value;
import org.molgenis.vkgl.clinvar.ClinVarHeaderEnum;

@Value
public class ClinVarHeaderMeta {
  ClinVarHeaderEnum type;
  int nrOfHeaders;
}
