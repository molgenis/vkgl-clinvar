package org.molgenis.vkgl.clinvar.model;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S115")
public enum Classification {
  b("Benign"), lb("Likely benign"), vus("Uncertain significance"), lp("Likely pathogenic"), p(
      "Pathogenic");

  private final String longName;

  Classification(String longName) {
    this.longName = longName;
  }

  public String getLongName() {
    return longName;
  }

  private static final Map<String, Classification> map = new HashMap<>(values().length, 1);

  static {
    for (Classification classification : values()) map.put(classification.getLongName(), classification);
  }

  public static Classification of(String name) {
    Classification result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Invalid classification name: " + name);
    }
    return result;
  }
}
