package org.molgenis.vkgl.clinvar.model;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S115")
public enum Classification {
  b("Benign", 1),
  lb("Likely benign", 2),
  vus("Uncertain significance", 3),
  lp("Likely pathogenic", 4),
  p("Pathogenic", 5),
  deleted("Deleted", -1);

  private final String longName;

  private final int numericValue;

  Classification(String longName, int numericValue) {
    this.longName = longName;
    this.numericValue = numericValue;
  }

  public int getNumericValue() {
    return numericValue;
  }

  public String getLongName() {
    return longName;
  }

  private static final Map<String, Classification> map = new HashMap<>(values().length, 1);

  static {
    for (Classification classification : values())
      map.put(classification.getLongName(), classification);
  }

  public static Classification of(String name) {
    Classification result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Invalid classification name: " + name);
    }
    return result;
  }
}
