package org.molgenis.vkgl.clinvar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vkgl.clinvar.model.Lab;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.VariantId;
import org.springframework.stereotype.Component;

@Component
public class ClinVarMapping {

  EnumMap<Lab, Map<VariantId, MappingLine>> mapping = new EnumMap<>(Lab.class);
  List<String> processedAccessions = new ArrayList<>();

  private Map<VariantId, MappingLine> getMapping(Lab lab) {
    mapping.computeIfAbsent(lab, k -> new HashMap<>());
    return mapping.get(lab);
  }

  public void addMapping(MappingLine mappingLine) {
    isAccessionUnique(mappingLine);
    Lab lab = Lab.valueOf(mappingLine.getLab());
    Map<VariantId, MappingLine> labMapping = getMapping(lab);
    labMapping.put(new VariantId(mappingLine), mappingLine);
    mapping.put(lab, labMapping);
  }

  private void isAccessionUnique(MappingLine mappingLine) {
    // Accession may not appear multiple time, not even with different version postfixes
    String mainAccessionId = mappingLine.getClinVarAccession().split("\\.")[0];
    if (processedAccessions.contains(mainAccessionId)) {
      throw new DuplicateAccessionException(mainAccessionId);
    } else {
      processedAccessions.add(mainAccessionId);
    }
  }

  public MappingLine getMapping(Lab lab, VariantId variantId) {
    Map<VariantId, MappingLine> labMapping = getMapping(lab);
    return labMapping.get(variantId);
  }

  public Collection<MappingLine> getAllMappingLines(Lab lab) {
    Map<VariantId, MappingLine> labMapping = getMapping(lab);
    return labMapping.values();
  }

  public boolean containsKey(Lab lab, VariantId variantId) {
    Map<VariantId, MappingLine> labMapping = getMapping(lab);
    return labMapping.containsKey(variantId);
  }
}
