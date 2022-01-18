package org.molgenis.vkgl.clinvar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.molgenis.vkgl.clinvar.model.GenesLine;
import org.molgenis.vkgl.clinvar.model.Settings;

public class GenesFilter {
  private final List<String> pseudogenes;

  public GenesFilter(Settings settings) {
    this.pseudogenes = readPseudogenes(settings.getPseudogenesPath());
  }

  private List<String> readPseudogenes(Path pseudogenesPath) {
    List<String> pseudogeneSymbols = new ArrayList<>();
    if (pseudogenesPath != null) {
      CsvReader<GenesLine> pseudogenesReader = new CsvReader<>();
      pseudogenesReader
          .read(pseudogenesPath, GenesLine.class)
          .forEach(line -> pseudogeneSymbols.add(line.getSymbol()));
    }
    return pseudogeneSymbols;
  }

  public String filter(String gene) {
    return pseudogenes.contains(gene) ? "" : gene;
  }
}
