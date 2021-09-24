package org.molgenis.vkgl.clinvar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.molgenis.vkgl.clinvar.model.MappingLine;
import org.molgenis.vkgl.clinvar.model.SubmissionLine;

public class DuplicateVariantUtil {

  private DuplicateVariantUtil() {}

  public static SubmissionLine getMostSevereVariant(Set<SubmissionLine> duplicates) {
    MappingLine mapping = null;
    for (SubmissionLine submissionLine : duplicates) {
      if (submissionLine.getClinVarAccession() != null
          && mapping != null
          && !mapping.getClinVarAccession().equals(submissionLine.getClinVarAccession())) {
        throw new DuplicateSubmittedAccessionException(
            mapping.getClinVarAccession(), submissionLine.getClinVarAccession());
      }

      mapping = submissionLine.getMappingLine();
      if (mapping != null) {
        mapping.setPartOfDuplicate(true);
      }
    }
    List<SubmissionLine> duplicatesList = new ArrayList<>(duplicates);
    duplicatesList.sort(new SubmissionLineComparator());
    SubmissionLine selectedSubmission = duplicatesList.get(0);
    selectedSubmission.setMappingLine(mapping);

    return selectedSubmission;
  }
}
