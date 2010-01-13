/*
 * Copyright (C) 2009-2010 Institute for Computational Biomedicine,
 *                         Weill Medical College of Cornell University
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.cornell.med.icb.iterators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

/**
 * Take a list of pre-sorted input files and return their lines in sorted order.
 * By default, duplicates and blank lines are skipped - which can be adjusted.
 * @author Kevin Dorff
 */
public class PreSortedMergeTextFilesLineIterator implements Iterable<String>, Iterator<String> {

    /** The line TextFileLineIterator iterators for the open files. */
    private Iterator<String>[] lineIterators;

    /** The next read but unused line for each of the open files. */
    private String[] nextLines;

    /**
     * The sorted list of nextLines so we know which to select from next. The size of
     * this will generally be the size of lineIterators.
     */
    private List<String> sortedList;

    /**
     * The last line that was returned with next(). This is kept so we don't
     * return the same value twice.
     */
    private String lastLine;

    /** If true, duplicates will be skipped. */
    private boolean skipDuplicates;

    /** If true, empty lines will be skipped. */
    private boolean skipEmptyLines;

    /**
     * Constructor with a String array of filenames.
     * @param inputFiles the filenames
     * @throws IOException error reading
     */
    public PreSortedMergeTextFilesLineIterator(final String[] inputFiles) throws IOException {
        initialize(inputFiles);
    }

    /**
     * Constructor for filenames OR streams in a list. If filenames, gziped (.gz suffixed filenames)
     * are seamlessly supported. NOTE: This assumes a list that is entirely List[String] or
     * List[InputStream] - this list cannot be a mix.
     * @param inputObjects the filenames / streams to read
     * @throws IOException error reading
     */
    @SuppressWarnings("unchecked")
    public PreSortedMergeTextFilesLineIterator(final List inputObjects) throws IOException {
        if (inputObjects == null) {
            throw new FileNotFoundException("No specified files");
        }
        if (inputObjects.size() == 0) {
            throw new FileNotFoundException("No specified files");
        }
        final Object inputFile = inputObjects.get(0);
        if (inputFile instanceof String) {
            initialize((String[]) inputObjects.toArray(new String[inputObjects.size()]));
        } else if (inputFile instanceof InputStream) {
            initialize((InputStream[]) inputObjects.toArray(new InputStream[inputObjects.size()]));
        } else {
            throw new IOException("Only List<String> and List<InputStream> supported.");
        }
    }

    /**
     * Constructor for input streams.
     * @param inputStreams the input streams to read
     * @throws IOException error reading
     */
    public PreSortedMergeTextFilesLineIterator(final InputStream[] inputStreams)
            throws IOException {
        initialize(inputStreams);
    }

    /**
     * Initialize with a String array of filenames.
     * @param inputFiles the filenames
     * @throws IOException error reading
     */
    private void initialize(final String[] inputFiles) throws IOException {
        final InputStream[] inputStreams = new InputStream[inputFiles.length];
        for (int pos = 0; pos < inputFiles.length; pos++) {
            final String inputFile = inputFiles[pos];
            final File inputFileFile = new File(inputFile);
            if (!(inputFileFile.exists() && inputFileFile.isFile())) {
                throw new FileNotFoundException("Input file " + inputFile + " does not exist.");
            }
            final InputStream is;
            if (inputFile.endsWith(".gz")) {
                is = new GZIPInputStream(new FileInputStream(inputFile));
            } else {
                is = new FileInputStream(inputFile);
            }
            inputStreams[pos] = is;
        }
        initialize(inputStreams);
    }

    /**
     * Initialize the reader. THE MAIN CONSTRUCTION OCCURS HERE.
     * @param inputStreams the input streams to read from
     * @throws IOException error reading (opening the TextFileLineIterators)
     */
    @SuppressWarnings("unchecked")
    private void initialize(final InputStream[] inputStreams) throws IOException {
        if (inputStreams.length == 0) {
            throw new FileNotFoundException("No specified files");
        }
        lineIterators = new Iterator[inputStreams.length];
        nextLines = new String[inputStreams.length];
        sortedList = new ArrayList<String>(inputStreams.length);
        int pos = 0;
        for (final InputStream inputStream : inputStreams) {
            lineIterators[pos++] = new TextFileLineIterator(inputStream).iterator();
        }
        lastLine = "";
        skipDuplicates = true;
        skipEmptyLines = true;
    }

    /**
     * If there is another line to be read.
     * @return true if there is another line to be read
     */
    public boolean hasNext() {
        fetchNexts();
        return someReadData();
        // No unread data. See if there os more data to read
    }

    /**
     * Retrieve the next line. If hasNext() is false this will throw a
     * NoSuchElementException.
     * @return the next line
     */
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in iterator, use hasNext()");
        }
        lastLine = sortedList.remove(0);
        if (skipDuplicates) {
            while (sortedList.contains(lastLine)) {
                // Remove dupliate lines
                sortedList.remove(lastLine);
            }
        }
        for (int i = 0; i < nextLines.length; i++) {
            if (nextLines[i] != null && nextLines[i].equals(lastLine)) {
                // Remove dupliate lines
                nextLines[i] = null;
            }
        }
        return lastLine;
    }

    /**
     * Retrieve the next non-blank, non-duplicate (from the last line returned)
     * line from each of the files that are still open. Files will be closed when
     * they have no more lines.
     */
    private void fetchNexts() {
        for (int i = 0; i < lineIterators.length; i++) {
            if ((nextLines[i] == null) && (lineIterators[i] != null)) {
                while (true) {
                    if (lineIterators[i].hasNext()) {
                        final String nextLine = lineIterators[i].next();
                        if (skipEmptyLines) {
                            if (nextLine.length() == 0) {
                                // Ignore empty lines
                                continue;
                            }
                        }
                        if (skipDuplicates) {
                            if (nextLine.equals(lastLine)) {
                                // Ignore duplicate lines
                                continue;
                            }
                        }
                        nextLines[i] = nextLine;
                        sortedList.add(nextLines[i]);
                        break;
                    } else {
                        lineIterators[i] = null;
                        break;
                    }
                }
            }
        }
        Collections.sort(sortedList);
    }

    /**
     * Returns true if these is some data that has been read (one of nextLines[]
     * is non-null).
     * @return if there is some data that has been read
     */
    private boolean someReadData() {
        for (int i = 0; i < lineIterators.length; i++) {
            if (nextLines[i] != null) {
                // We at least have data we have already read
                return true;
            }
        }
        return false;
    }

    /**
     * This is the one method of the Iterable interface.
     * @return the iterator
     */
    public Iterator<String> iterator() {
        return this;
    }

    /**
     * Remove not supported.
     * The file is read-only; we don't allow lines to be removed.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get if empty lines will be skipped.
     * @return if empty lines will be skipped.
     */
    public boolean isSkipEmptyLines() {
        return skipEmptyLines;
    }

    /**
     * Set if empty lines will be skipped.
     * @param skipEmptyLines if empty lines will be skipped.
     */
    public void setSkipEmptyLines(final boolean skipEmptyLines) {
        this.skipEmptyLines = skipEmptyLines;
    }

    /**
     * Get if duplicates will be skipped.
     * @return if duplicates will be skipped.
     */
    public boolean isSkipDuplicates() {
        return skipDuplicates;
    }

    /**
     * Set if duplicates will be skipped.
     * @param skipDuplicates if duplicates will be skipped.
     */
    public void setSkipDuplicates(final boolean skipDuplicates) {
        this.skipDuplicates = skipDuplicates;
    }
}
