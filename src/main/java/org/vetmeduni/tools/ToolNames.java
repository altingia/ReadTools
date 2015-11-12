/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Daniel Gómez-Sánchez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 */
package org.vetmeduni.tools;

import org.vetmeduni.methods.barcodes.BarcodeMethods;
import org.vetmeduni.tools.implemented.*;

/**
 * Enum with all the tools already developed
 *
 * @author Daniel Gómez-Sánchez
 */
public enum ToolNames {
	TrimFastq("Implementation of the trimming algorithm from Kofler et al. (2011)",
		"The program removes 'N' - characters at the beginning and the end of the provided reads. If any remaining 'N' "
			+ "characters are found the read is discarded. Quality removal is done using a modified Mott-algorithm: for "
			+ "each base a score is calculated (score_base = quality_base - threshold). While scanning along the read "
			+ "a running sum of this score is calculated; If the score drops below zero the score is set to zero; The "
			+ "highest scoring region of the read is finally reported.\n\nCitation of the method: Kofler et al. (2011), "
			+ "PLoS ONE 6(1), e15925, doi:10.1371/journal.pone.0015925", new TrimFastq()),
	BarcodedBamToFastq("Convert an BAM file with BC tags into a FASTQ file",
		"Because some services provide a barcoded BAM file instead of a FASTQ this tool convert them into the later to be "
			+ "allow to use it. It works with one or two barcodes, pair-end (interleaved BAM file) and single-end sequencing."
			+ " In addition, it match the barcodes with the used ones to discard some reads that could not be assign, and"
			+ " add the exact detected barcode to the read name. The method to assing barcodes is the following: if there "
			+ "is an exact match for an unique barcode, it is directly assign; if there are more than 1 barcode, it assign "
			+ "it to the sample where most barcodes match; otherwise, it is discarded. Barcodes in the input file that are "
			+ "larger than the used ones are cut in the last bases.", new BarcodedBamToFastq()),
	QualityChecker("Get the quality encoding for a BAM/FASTQ file",
		"Check the quality encoding for a BAM/FASTQ file and output in the STDOUT the encoding", new QualityChecker()),
	StandardizeQuality("Convert an Illumina BAM/FASTQ file into a Sanger",
		"The standard encoding for a BAM file is Sanger and this tool is provided to standardize both BAM/FASTQ files "
			+ "for latter analysis. It does not support mixed qualities", new StandardizeQuality()),
	FastqBarcodeDetector(
		"Identify barcodes in the read name for a FASTQ file and assign to the ones used on the library",
		"Detect barcodes in the header of the read name (based on the marker " + BarcodeMethods.BARCODE_SEPARATOR
			+ ") of a FASTQ file and assign to a sample based on a provided dictionary. Barcodes in the input file that "
			+ "are larger than the used ones are cut in the last bases.", new FastqBarcodeDetector());

	/**
	 * The short description for the tool
	 */
	public final String shortDescription;

	/**
	 * The long description for the tool
	 */
	public final String fullDescription;

	private final Tool associatedTool;

	/**
	 * Constructor
	 *
	 * @param shortDescription the short description
	 * @param fullDescription  the full description
	 * @param associatedTool   the tool to run
	 */
	ToolNames(String shortDescription, String fullDescription, Tool associatedTool) {
		this.shortDescription = shortDescription;
		this.fullDescription = fullDescription;
		this.associatedTool = associatedTool;
	}

	/**
	 * Get the tool class from enums
	 *
	 * @param tool the tool as a String
	 *
	 * @return a new instance of a tool
	 */
	public static Tool getTool(String tool) throws IllegalArgumentException {
		return ToolNames.valueOf(tool).getTool();
	}

	/**
	 * Get the associated tool for this toolname
	 *
	 * @return the associated tool
	 */
	public Tool getTool() {
		return associatedTool;
	}
}
