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
package org.magicdgs.io.writers.fastq;

import org.magicdgs.io.FastqPairedRecord;

import htsjdk.samtools.fastq.FastqWriter;

/**
 * Interface for FastqWriter that allow use all teh classes implemented as FastqWriters in the same
 * interface
 *
 * @author Daniel Gómez-Sánchez
 */
public interface ReadToolsFastqWriter extends FastqWriter {

    /**
     * Write a FastqPairedRecord in this writer
     *
     * @param rec the record to write
     *
     * @throws java.lang.UnsupportedOperationException if the writer is not pair-end
     */
    void write(final FastqPairedRecord rec) throws UnsupportedOperationException;
}
