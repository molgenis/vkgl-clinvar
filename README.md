## Usage
```
usage: java -jar vkgl-clinvar-writer.jar -i <arg> [-m <arg>] [-c <arg>] -o
       <arg> -r <arg> [-dd] [-s] [-f] [-d]
 -i,--input <arg>          VKGL consensus file (tsv).
 -m,--mappings <arg>       Mapping log files from previous run with this
                           tool.
 -c,--clinVar <arg>        ClinVar Submission reports from previous
                           submission, format: lab1:report
                           path,lab2:report path
 -o,--output <arg>         Directory to write output to.
 -r,--release <arg>        Release name to use in ClinVar file names.
 -dd,--delete_duplicated   Flag to indicate is existing duplicated should
                           be deleted.
 -s,--include_single_lab   Flag to indicate that single lab submissions
                           should also be submitted.
 -f,--force                Override the output files if output directory
                           already exists.
 -d,--debug                Enable debug mode (additional logging).

usage: java -jar vkgl-clinvar-writer.jar -v
 -v,--version   Print version.
```

##Existing ID's

For the correct submission of updates of existing records please provide the following files:
- Log files of the previous run of this tool that were not submitted to ClinVar (Unchanged, duplicates, deletes if they were not submitted).
- ClinVar submission reports for all labs that were submitte.
The flow of this tool expects that all labs are submitted every cycle, if submits for a subset of labs were performed some manual work may be needed.
e.g. adding the "Updated" log as well but removing lines for the labs that were submitted from it first.

##Current limitation - No SV support
Variant longer than 15 nucleotides are not submitted, ClinVar has slightly different rules for these variants that are not yet implemented.

##Deletes
A ```.Deletes``` sheet is created for every lab, containing accessions that are no longer present or valid.
Accession can become invalid if:
- The consensusline is based on a single lab and the "include single lab" flag is false.
- A duplicate (same chrom pos ref alt) was provided for the same lab, and the "delete duplicates" flag is true.
- If the consensus status has become "disagreement" or "total_disagreement"

##Duplicates
Setting the --delete_duplicated flag will remove ClinVar accession that have been duplicated.
Please note that duplicates are **not added** if the flag is not set.

##Mapping
A manually constructed mapping file for the first ClinVar release is available.

The log files:
- *_DUPLICATED_log
- *_REMOVED_log
- *_UNCHANGED_log
- *_UPDATED_log

are formatted in the same format as the mapping file (as provided for the --mapping_file option) for the existing ClinVar Accessions, these files can be used in combination with the ClinVar submission reports to create a mapping file for the next release.
Which of these files should be included depends on the "delete duplicates" flag and if both Variant and Deletes sheets were submitted.

Tooling to support this should be created once we have ClinVar submission reports from the next submission.
