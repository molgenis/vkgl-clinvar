package org.molgenis.vkgl.clinvar;

import java.util.Comparator;
import org.molgenis.vkgl.clinvar.model.SubmissionLine;

public class SubmissionLineComparator implements Comparator<SubmissionLine> {

  @Override
  public int compare(SubmissionLine line1, SubmissionLine line2) {
    return sortOnClassification(line1, line2);
  }

  private int sortOnClassification(SubmissionLine line1, SubmissionLine line2) {
    int line1Class = line1.getConsensusLine().getClassification(line1.getLab()).getNumericValue();
    int line2Class = line2.getConsensusLine().getClassification(line2.getLab()).getNumericValue();
    if (line1Class < line2Class) {
      return 1;
    } else if (line1Class > line2Class) {
      return -1;
    } else {
      return sortOnGene(line1, line2);
    }
  }

  private int sortOnGene(SubmissionLine line1, SubmissionLine line2) {
    return line1.getConsensusLine().getGene().compareTo(line2.getConsensusLine().getGene());
  }
}
