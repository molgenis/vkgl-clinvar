package org.molgenis.vkgl.clinvar.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Settings {

  Path input;
  List<Path> mappings;
  Map<Lab, Path> clinVarMapping;
  Path deletes;
  Path outputDir;
  boolean overwrite;
  boolean debug;
  boolean includeSingleLab;
  String release;
}
