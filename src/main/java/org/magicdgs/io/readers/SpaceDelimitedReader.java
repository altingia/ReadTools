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
package org.magicdgs.io.readers;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Reader for tab-delimited/space-delimited files using a scanner
 *
 * @author Daniel Gómez-Sánchez
 */
public class SpaceDelimitedReader implements Closeable {

    /**
     * The underlying scanner
     */
    private final Scanner scanner;

    /**
     * Constructor for a file
     *
     * @param file the file
     */
    public SpaceDelimitedReader(final Path file) throws IOException {
        scanner = new Scanner(Files.newInputStream(file));
    }

    /**
     * Check if the file have a next line
     *
     * @return <code>true</code> if the file is not finished; <code>false</code> otherwise
     */
    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    /**
     * Read the next line
     *
     * @return the tokens that are separated by tabs; <code>null</code> if no more lines
     */
    public String[] next() {
        if (hasNext()) {
            String line = scanner.nextLine();
            return line.split("\\s+");
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        scanner.close();
    }
}
