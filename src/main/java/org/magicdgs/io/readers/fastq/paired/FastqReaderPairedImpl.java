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
package org.magicdgs.io.readers.fastq.paired;

import htsjdk.samtools.fastq.FastqReader;
import htsjdk.samtools.util.FastqQualityFormat;
import org.magicdgs.io.FastqPairedRecord;
import org.magicdgs.utils.fastq.QualityUtils;

import java.io.File;

/**
 * Implementation for pair-end reader with two files
 *
 * @author Daniel Gómez-Sánchez
 */
public class FastqReaderPairedImpl extends FastqReaderPairedAbstract {

	public FastqReaderPairedImpl(FastqReader reader1, FastqReader reader2, boolean allowHighQualities) throws QualityUtils.QualityException {
		super(reader1, reader2, allowHighQualities);
	}

	public FastqReaderPairedImpl(File reader1, File reader2, boolean allowHighQualities) throws QualityUtils.QualityException {
		super(reader1, reader2, allowHighQualities);
	}

	@Override
	public FastqQualityFormat getFastqQuality() {
		return getOriginalEncoding();
	}

	@Override
	public FastqPairedRecord next() {
		final FastqPairedRecord toReturn = nextUnchangedRecord();
		checker.checkMisencoded(toReturn);
		return toReturn;
	}
}
