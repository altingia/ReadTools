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
package org.vetmeduni.tools.implemented;

import htsjdk.samtools.util.FastqQualityFormat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.vetmeduni.tools.AbstractTool;
import org.vetmeduni.utils.fastq.QualityUtils;

import java.io.File;

import static org.vetmeduni.tools.ToolNames.ToolException;
import static org.vetmeduni.tools.cmd.OptionUtils.getUniqueValue;

/**
 * Tool for check the quality in both FASTQ and BAM files
 *
 * @author Daniel Gómez-Sánchez
 */
public class QualityChecker extends AbstractTool {

	@Override
	protected void runThrowingExceptions(CommandLine cmd) throws Exception {
		// TODO: check the qualities for the reader completely
		File input = new File(getUniqueValue(cmd, "i"));
		long recordsToIterate;
		try {
			String toIterate = getUniqueValue(cmd, "m");
			recordsToIterate = (toIterate == null) ?
				QualityUtils.DEFAULT_MAX_RECORDS_TO_DETECT_QUALITY :
				Long.parseLong(toIterate);
			if (recordsToIterate < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new ToolException("Number of reads should be a positive long");
		}
		logCmdLine(cmd);
		FastqQualityFormat format = QualityUtils.getFastqQualityFormat(input, recordsToIterate);
		String toConsole = (format == FastqQualityFormat.Standard) ? "Sanger" : "Illumina";
		System.out.println(toConsole);
	}

	@Override
	protected Options programOptions() {
		Option input = Option.builder("i").longOpt("input").desc("Input BAM/FASTQ to determine the quality").hasArg()
							 .numberOfArgs(1).argName("INPUT").required().build();
		Option max = Option.builder("m").longOpt("maximum-reads").desc(
			"Maximum number of read to use to iterate. [Default=" + QualityUtils.DEFAULT_MAX_RECORDS_TO_DETECT_QUALITY
				+ "]").hasArg().numberOfArgs(1).argName("LONG").required(false).build();
		Options options = new Options();
		options.addOption(input);
		options.addOption(max);
		return options;
	}
}
