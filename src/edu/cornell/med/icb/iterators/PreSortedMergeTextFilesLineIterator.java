/*
 * Copyright (C) 2009 Institute for Computational Biomedicine,
 *                    Weill Medical College of Cornell University
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
 * NOTE: Blank lines are INTENTIONALLY ignored. Duplicates will only be returned ONCE.
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

    /**
     * Constructor for filenames. This automatically supports .gz'ipped files if the
     * filename ends in .gz.
     * @param inputFiles the filenames to read
     * @throws IOException error reading
     */
    public PreSortedMergeTextFilesLineIterator(final String[] inputFiles) throws IOException {
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
     * Constructor for input streams.
     * @param inputStreams the input streams to read
     * @throws IOException error reading
     */
    public PreSortedMergeTextFilesLineIterator(final InputStream[] inputStreams)
            throws IOException {
        initialize(inputStreams);
    }

    /**
     * Initialize the reader.
     * @param inputStreams the input streams to read from
     * @throws IOException error reading (opening the TextFileLineIterators)
     */
    @SuppressWarnings("unchecked")
    private void initialize(final InputStream[] inputStreams) throws IOException {
        lineIterators = new Iterator[inputStreams.length];
        nextLines = new String[inputStreams.length];
        sortedList = new ArrayList<String>(inputStreams.length);
        int pos = 0;
        for (final InputStream inputStream : inputStreams) {
            lineIterators[pos++] = new TextFileLineIterator(inputStream).iterator();
        }
        lastLine = "";
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
        while (sortedList.contains(lastLine)) {
            // Remove dupliate lines
            sortedList.remove(lastLine);
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
                        if (nextLine.length() == 0) {
                            // Ignore empty lines
                            continue;
                        }
                        if (nextLine.equals(lastLine)) {
                            // Ignore duplicate lines
                            continue;
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
}
