/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Daniel Gómez-Sánchez
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

package org.magicdgs.readtools.tools.trimming;

import org.magicdgs.readtools.RTDefaults;
import org.magicdgs.readtools.cmd.RTStandardArguments;
import org.magicdgs.readtools.cmd.argumentcollections.RTOutputArgumentCollection;
import org.magicdgs.readtools.cmd.plugin.TrimmerPluginDescriptor;
import org.magicdgs.readtools.cmd.programgroups.ReadToolsProgramGroup;
import org.magicdgs.readtools.engine.ReadToolsWalker;
import org.magicdgs.readtools.metrics.FilterMetric;
import org.magicdgs.readtools.metrics.TrimmerMetric;
import org.magicdgs.readtools.utils.read.ReservedTags;
import org.magicdgs.readtools.utils.read.transformer.trimming.MottQualityTrimmer;
import org.magicdgs.readtools.utils.read.transformer.trimming.TrailingNtrimmer;
import org.magicdgs.readtools.utils.read.writer.NullGATKWriter;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.metrics.MetricsFile;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.samtools.util.Histogram;
import org.broadinstitute.barclay.argparser.Argument;
import org.broadinstitute.barclay.argparser.ArgumentCollection;
import org.broadinstitute.barclay.argparser.CommandLinePluginDescriptor;
import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
import org.broadinstitute.hellbender.cmdline.GATKPlugin.GATKReadFilterPluginDescriptor;
import org.broadinstitute.hellbender.engine.filters.ReadLengthReadFilter;
import org.broadinstitute.hellbender.exceptions.UserException;
import org.broadinstitute.hellbender.utils.read.GATKRead;
import org.broadinstitute.hellbender.utils.read.GATKReadWriter;
import scala.Tuple2;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tool for perform a trimming/filtering pipeline.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
@CommandLineProgramProperties(oneLineSummary = "Applies a trimming pipeline to any kind of sources for ReadTools",
        summary = "Apply a trimming/filtering pipeline to the reads as following:\n"
                + "\t- Trimmers are applied in order. If any read is trimmed completely, the rest are ignored.\n"
                + "\t- Filter out completely trim reads.\n"
                + "\t- Apply the filters in order. If any read is filtered, the 'FT' tag reflects the reason.\n"
                + "Default arguments perform the same algorithm as the one described in Kofler et al. (PLoS ONE 6, 2011, e15925)."
                + " Other features in the pipeline implemented there could be apply with some minor modifications in the command line.\n"
                + "\nNote: default trimmer(s)/filter(s) are applied before any specified by the user."
                + " If you would like to apply them in a different order, use --"
                + RTStandardArguments.DISABLE_ALL_DEFAULT_TRIMMERS_NAME
                + " in combination with the new ordering.",
        programGroup = ReadToolsProgramGroup.class)
public final class TrimReads extends ReadToolsWalker {

    @ArgumentCollection
    public RTOutputArgumentCollection outputBamArgumentCollection =
            RTOutputArgumentCollection.defaultOutput();

    @Argument(fullName = RTStandardArguments.KEEP_DISCARDED_NAME, shortName = RTStandardArguments.KEEP_DISCARDED_NAME, optional = true, doc = "Keep discarded reads in a separate file. Note: For pair-end input, this file contain also mates of discarded reads (they do not have FT tag).")
    public boolean keepDiscarded = false;

    // defaults filters as in the deprecated TrimFastq tool
    // for discard the ambiguous sequences (--discard-internal-N in the deprecated TrimFastq)
    // use --readFilter AmbiguousBaseReadFilter --ambigFilterFrac 0
    @Override
    protected List<? extends CommandLinePluginDescriptor<?>> getPluginDescriptors() {
        return Arrays.asList(
                new TrimmerPluginDescriptor(
                        Arrays.asList(new TrailingNtrimmer(), new MottQualityTrimmer())),
                new GATKReadFilterPluginDescriptor(Collections
                        .singletonList(new ReadLengthReadFilter(40, Integer.MAX_VALUE))));
    }

    // pipeline to trim and filter
    private TrimAndFilterPipeline pipeline;

    // writers -> keep instance to close
    private GATKReadWriter writer;
    private GATKReadWriter discardedWriter;

    // common metrics
    private List<Histogram<Integer>> lengthHistogramsBeforeTrimming;
    private List<Histogram<Integer>> lengthHistogramsAfterTrimming;

    @Override
    public void onTraversalStart() {
        // set the length histograms
        if (isPaired()) {
            lengthHistogramsBeforeTrimming = Arrays.asList(
                    new Histogram<>("length", "first_before"),
                    new Histogram<>("length", "second_before"));
            lengthHistogramsAfterTrimming = Arrays.asList(
                    new Histogram<>("length", "first_after"),
                    new Histogram<>("length", "second_after"));
        } else {
            lengthHistogramsBeforeTrimming =
                    Collections.singletonList(new Histogram<>("length", "before"));
            lengthHistogramsAfterTrimming =
                    Collections.singletonList(new Histogram<>("length", "after"));
        }

        // initialize the pipeline with the plugin descriptors
        pipeline = TrimAndFilterPipeline.fromPluginDescriptors(
                commandLineParser.getPluginDescriptor(TrimmerPluginDescriptor.class),
                commandLineParser.getPluginDescriptor(GATKReadFilterPluginDescriptor.class));

        // get the writers
        final SAMFileHeader header = getHeaderForReads();

        // setup the writer
        writer = outputBamArgumentCollection.outputWriter(header,
                () -> getProgramRecord(header), true, getReferenceFile()
        );

        if (keepDiscarded) {
            discardedWriter = outputBamArgumentCollection.getWriterFactory()
                    .setReferenceFile(getReferenceFile())
                    .createWriter(outputBamArgumentCollection
                                    .getOutputNameWithSuffix(RTDefaults.DISCARDED_OUTPUT_SUFFIX),
                            getHeaderForReads(), true);
        } else {
            discardedWriter = new NullGATKWriter();
        }
    }

    @Override
    protected void apply(final GATKRead read) {
        if (testRead(read, 0)) {
            // if it pass, send to the writer
            writePassing(read, 0);
        } else {
            writeDiscarded(read);
        }
    }

    @Override
    protected void apply(final Tuple2<GATKRead, GATKRead> pair) {
        // test reads
        final boolean firstPass = testRead(pair._1, 0);
        final boolean secondPass = testRead(pair._2, 1);

        if (firstPass && secondPass) {
            writePassing(pair._1, 0);
            writePassing(pair._2, 1);
        } else {
            writeDiscarded(pair._1);
            writeDiscarded(pair._2);
        }
    }

    // test the read and updates the length histogram
    private boolean testRead(final GATKRead read, final int index) {
        lengthHistogramsBeforeTrimming.get(index).increment(read.getLength());
        return pipeline.test(read);
    }

    // write the read and updates the length histogram
    private void writePassing(final GATKRead passingRead, final int index) {
        lengthHistogramsAfterTrimming.get(index).increment(passingRead.getLength());
        // TODO: this should be moved to the default apply trimming result if it is not trimmed
        passingRead.clearAttribute(ReservedTags.ct);
        writer.addRead(passingRead);
    }

    private void writeDiscarded(final GATKRead discardedRead) {
        // TODO: this should be moved inside the if when the trimming result is cleared
        discardedRead.clearAttribute(ReservedTags.ct);
        discardedWriter.addRead(discardedRead);
    }

    /**
     * Prints the statistics for each barcode into a metrics file and logs the number of records
     * per barcode.
     */
    @Override
    public Object onTraversalSuccess() {
        // TODO: maybe we should create a metric file per statistic
        final Path path = outputBamArgumentCollection.makeMetricsFile(null);
        try (final Writer metricsWriter = Files.newBufferedWriter(path)) {
            // trimer metrics with the header
            final MetricsFile<TrimmerMetric, Integer> trimming = getMetricsFile();
            trimming.addAllMetrics(pipeline.getTrimmingStats());
            trimming.write(metricsWriter);

            // filter metrics with histogram without header
            final MetricsFile<FilterMetric, Integer> filtering = new MetricsFile<>();
            filtering.addAllMetrics(pipeline.getFilterStats());
            lengthHistogramsBeforeTrimming.forEach(filtering::addHistogram);
            lengthHistogramsAfterTrimming.forEach(filtering::addHistogram);
            filtering.write(metricsWriter);

        } catch (IOException e) {
            throw new UserException.CouldNotCreateOutputFile(path.toString(), e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void closeTool() {
        // close the three writers
        CloserUtil.close(writer);
        CloserUtil.close(discardedWriter);
    }
}