package org.molgenis.vkgl.clinvar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.VariantGeneId;
import org.springframework.stereotype.Component;

@Component
public class ClinVarMapping {

  EnumMap<Lab, Map<VariantGeneId, MappingLine>> mapping = new EnumMap<>(Lab.class);
  List<String> processedAccessions = new ArrayList<>();

  private Map<VariantGeneId, MappingLine> getMapping(Lab lab) {
    if (!mapping.containsKey(lab)) {
      Map<VariantGeneId, MappingLine> labMapping = new HashMap<>();
      mapping.put(lab, labMapping);
    }
    return mapping.get(lab);
  }

  public void addMapping(MappingLine mappingLine) {
    isAccessionUnique(mappingLine);
    Lab lab = Lab.valueOf(mappingLine.getLab());
    Map<VariantGeneId, MappingLine> labMapping = getMapping(lab);
    labMapping.put(new VariantGeneId(mappingLine), mappingLine);
    mapping.put(lab, labMapping);
  }

  private void isAccessionUnique(MappingLine mappingLine) {
    //Accession may not appear multiple time, not even with different version postfixes
    String mainAccessionId = mappingLine.getClinVarAccession().split("\\.")[0];
    if (processedAccessions.contains(mainAccessionId)) {
      throw new DuplicateAccessionException(mainAccessionId);
    } else {
      processedAccessions.add(mainAccessionId);
    }
  }

  public MappingLine getMapping(Lab lab, VariantGeneId variantGeneId) {
    Map<VariantGeneId, MappingLine> labMapping = getMapping(lab);
    return labMapping.get(variantGeneId);
  }

  public Collection<MappingLine> getAllMappingLines(Lab lab) {
    Map<VariantGeneId, MappingLine> labMapping = getMapping(lab);
    return labMapping.values();
  }

  public boolean containsKey(Lab lab, VariantGeneId variantGeneId) {
    Map<VariantGeneId, MappingLine> labMapping = getMapping(lab);
    return labMapping.containsKey(variantGeneId);
  }
}
