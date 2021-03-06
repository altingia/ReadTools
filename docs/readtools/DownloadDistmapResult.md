---
title: DownloadDistmapResult
summary: Download, sort and merge the alignments generated by DistMap.
permalink: DownloadDistmapResult.html
last_updated: 04-49-2017 12:49:37
---

## Description

Download, sort and merge the alignments generated by DistMap
 (<a href="http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0072614">Pandey
 &amp; Schlötterer 2013</a>).

 <p>This tool scan the folder provided as input for multi-part BAM/SAM/CRAM files (e.g. 'part-*'),
 sort and merge them by batches (in the temp directory) and finally merge all the barches into a
 single output file.
 </p>

{% include note.html content='The resulsts are expected to be located in the Hadoop FileSystem (HDFS) and the
 output file in the local computer for following usage, but it is not required.' %}

## Arguments

### Required Arguments

| Argument name(s) | Type | Description |
| :--------------- | :--: | :------ |
| `--input`<br/>`-I` | String | Input folder to look for Distmap multi-part file results. Expected to be in an HDFS file system. |
| `--output`<br/>`-O` | String | Output SAM/BAM/CRAM file. |

### Optional Arguments

| Argument name(s) | Type | Default value(s) | Description |
| :--------------- | :--: | :--------------: | :------ |
| `--arguments_file` | List[File] | [] | read one or more arguments files and add them to the command line |
| `--gcs_max_retries`<br/>`-gcs_retries` | int | 20 | If the GCS bucket channel errors out, how many times it will attempt to re-initiate the connection |
| `--help`<br/>`-h` | boolean | false | display the help message |
| `--version` | boolean | false | display the version number for this tool |

### Optional Common Arguments

| Argument name(s) | Type | Default value(s) | Description |
| :--------------- | :--: | :--------------: | :------ |
| `--addOutputSAMProgramRecord`<br/>`-addOutputSAMProgramRecord` | boolean | true | If true, adds a PG tag to created SAM/BAM/CRAM files. |
| `--createOutputBamIndex`<br/>`-OBI` | boolean | true | If true, create a BAM/CRAM index when writing a coordinate-sorted BAM/CRAM file. |
| `--createOutputBamMD5`<br/>`-OBM` | boolean | false | If true, create a MD5 digest for any BAM/SAM/CRAM file created |
| `--forceOverwrite`<br/>`-forceOverwrite` | Boolean | false | Force output overwriting if it exists |
| `--QUIET` | Boolean | false | Whether to suppress job-summary info on System.err. |
| `--readValidationStringency`<br/>`-VS` | ValidationStringency | SILENT | Validation stringency for all SAM/BAM/CRAM files read by this program. The default stringency value SILENT can improve performance when processing a BAM file in which variable-length data (read, qualities, tags) do not otherwise need to be decoded.<br/><br/><b>Possible values:</b> <i>STRICT</i>, <i>LENIENT</i>, <i>SILENT</i> |
| `--reference`<br/>`-R` | File | null | Reference sequence file. Required for CRAM input/output. |
| `--secondsBetweenProgressUpdates`<br/>`-secondsBetweenProgressUpdates` | double | 10.0 | Output traversal statistics every time this many seconds elapse. |
| `--SORT_ORDER`<br/>`-SO` | SortOrder | coordinate | Sort order of output file<br/><br/><b>Possible values:</b> <i>unsorted</i>, <i>queryname</i>, <i>coordinate</i>, <i>duplicate</i>, <i>unknown</i> |
| `--TMP_DIR` | List[File] | [] | Undocumented option |
| `--use_jdk_deflater`<br/>`-jdk_deflater` | boolean | false | Whether to use the JdkDeflater (as opposed to IntelDeflater) |
| `--use_jdk_inflater`<br/>`-jdk_inflater` | boolean | false | Whether to use the JdkInflater (as opposed to IntelInflater) |
| `--verbosity`<br/>`-verbosity` | LogLevel | INFO | Control verbosity of logging.<br/><br/><b>Possible values:</b> <i>ERROR</i>, <i>WARNING</i>, <i>INFO</i>, <i>DEBUG</i> |

### Advanced Arguments

| Argument name(s) | Type | Default value(s) | Description |
| :--------------- | :--: | :--------------: | :------ |
| `--noRemoveTaskProgramGroup` | boolean | false | Do not remove the @PG lines generated by every task in the MapReduce Distmap run. |
| `--numberOfParts` | int | 100 | Number of part files to download, merge and pre-sort at the same time. Reduce this number if you have memory errors. |
| `--showHidden`<br/>`-showHidden` | boolean | false | display hidden arguments |


