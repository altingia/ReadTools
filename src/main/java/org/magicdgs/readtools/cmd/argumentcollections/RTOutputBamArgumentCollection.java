/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Daniel Gómez-Sánchez
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
 * SOFTWARE.
 */

package org.magicdgs.readtools.cmd.argumentcollections;

import org.magicdgs.readtools.utils.misc.IOUtils;
import org.magicdgs.readtools.utils.read.ReadWriterFactory;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMProgramRecord;
import org.broadinstitute.hellbender.cmdline.Argument;
import org.broadinstitute.hellbender.cmdline.StandardArgumentDefinitions;
import org.broadinstitute.hellbender.exceptions.UserException;
import org.broadinstitute.hellbender.utils.read.GATKReadWriter;

import java.io.File;
import java.util.function.Supplier;

/**
 * Output argument collection for output SAM/BAM/CRAM files.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
final class RTOutputBamArgumentCollection extends RTOutputArgumentCollection {
    private static final long serialVersionUID = 1L;

    @Argument(fullName = StandardArgumentDefinitions.OUTPUT_LONG_NAME, shortName = StandardArgumentDefinitions.OUTPUT_SHORT_NAME, doc = "Output SAM/BAM/CRAM file.", optional = false)
    public String outputName;

    @Argument(fullName = StandardArgumentDefinitions.CREATE_OUTPUT_BAM_INDEX_LONG_NAME, shortName = StandardArgumentDefinitions.CREATE_OUTPUT_BAM_INDEX_SHORT_NAME, doc = "If true, create a BAM/CRAM index when writing a coordinate-sorted BAM/CRAM file.", optional = true)
    public boolean createOutputBamIndex = true;

    @Argument(fullName = StandardArgumentDefinitions.CREATE_OUTPUT_BAM_MD5_LONG_NAME, shortName = StandardArgumentDefinitions.CREATE_OUTPUT_BAM_MD5_SHORT_NAME, doc = "If true, create a MD5 digest for any BAM/SAM/CRAM file created", optional = true)
    public boolean createOutputBamMD5 = false;

    @Argument(fullName = "addOutputSAMProgramRecord", shortName = "addOutputSAMProgramRecord", doc = "If true, adds a PG tag to created SAM/BAM/CRAM files.", optional = true)
    public boolean addOutputSAMProgramRecord = true;

    /** Gets the writer factory for the arguments, adding also the reference file. */
    @Override
    protected ReadWriterFactory getWriterFactory() {
        return super.getWriterFactory()
                .setForceOverwrite(forceOverwrite)
                .setCreateIndex(createOutputBamIndex)
                .setCreateMd5File(createOutputBamMD5);
    }

    /**
     * Checks if the output name is a SAM/BAM/CRAM file and if so it creates a SAM writer.
     * Otherwise, it thrown an UserException.
     */
    @Override
    protected GATKReadWriter createWriter(final ReadWriterFactory factory,
            final SAMFileHeader header, final boolean presorted) {
        if (!IOUtils.isSamBamOrCram(outputName)) {
            // TODO: update after https://github.com/broadinstitute/gatk/pull/2282
            throw new UserException.CouldNotCreateOutputFile(new File(outputName),
                    "The output file should have a BAM/SAM/CRAM extension.");
        }
        return factory.createSAMWriter(outputName, header, presorted);
    }

    /**
     * Updates the header with the program record if {@link #addOutputSAMProgramRecord} is
     * {@code true} and the supplier is not {@code null}.
     */
    @Override
    protected void updateHeader(final SAMFileHeader header,
            final Supplier<SAMProgramRecord> programRecord) {
        if (addOutputSAMProgramRecord && programRecord != null) {
            header.addProgramRecord(programRecord.get());
        }
    }
}