/*
 * Copyright (C) 2008-2010 Institute for Computational Biomedicine,
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

import edu.cornell.med.icb.io.TsvToFromMap;
import edu.cornell.med.icb.maps.LinkedHashToMultiTypeMap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Iterate a tsv file using a specified TsvToFromMap. This will completely ignore the first
 * non-comment line, assuming it to be a header line. This will also ignore all comment lines
 * (lines that start with '#').
 */
public class TsvLineIterator implements Iterable<LinkedHashToMultiTypeMap<String>>, Closeable {

    private static final String COMMENT_CHAR = "#";

    /** The reader we're using. */
    private final TextFileLineIterator textFileLineIterator;

    /** The file being read. */
    private final Iterator<String> iterator;

    /** The file being read. */
    private final TsvToFromMap tsvReader;

    /**
     * Create the object using a filename. This will read the header line to determine the
     * column headers from the file.
     * @param filenameToRead the filename to read
     * @throws java.io.IOException error opening the file to read
     */
    public TsvLineIterator(final String filenameToRead) throws IOException {
        this(new File(filenameToRead));
    }

    /**
     * Create the object using a file.
     * @param fileToRead the filename to read
     * @throws java.io.IOException error opening the file to read
     */
    public TsvLineIterator(final File fileToRead) throws IOException {
        this.tsvReader = TsvToFromMap.createFromTsvFile(fileToRead);
        this.textFileLineIterator = new TextFileLineIterator(fileToRead);
        this.iterator = textFileLineIterator.iterator();
    }

    /**
     * Create the object using a filename.
     * @param filenameToRead the filename to read
     * @param tsvReader the TsvToFroMap to assist with reading the tsv file
     * @throws java.io.IOException error opening the file to read
     */
    public TsvLineIterator(final String filenameToRead, final TsvToFromMap tsvReader) throws IOException {
        this(new File(filenameToRead), tsvReader);
    }

    /**
     * Create the object using a file.
     * @param fileToRead the filename to read
     * @param tsvReader the TsvToFroMap to assist with reading the tsv file
     * @throws java.io.IOException error opening the file to read
     */
    public TsvLineIterator(final File fileToRead, final TsvToFromMap tsvReader) throws IOException {
        this.textFileLineIterator = new TextFileLineIterator(fileToRead);
        this.iterator = textFileLineIterator.iterator();
        this.tsvReader = tsvReader;
    }

    /**
     * Create the object using an InputStream.
     * @param stream the stream for the file to read
     * @param tsvReader the TsvToFroMap to assist with reading the tsv file
     * @throws java.io.IOException error opening the file to read
     */
    public TsvLineIterator(final InputStream stream, final TsvToFromMap tsvReader) throws IOException {
        this.textFileLineIterator = new TextFileLineIterator(stream);
        this.iterator = textFileLineIterator.iterator();
        this.tsvReader = tsvReader;
    }

    /**
     * This is the one method of the Iterable interface.
     * @return the iterator
     */
    public Iterator<LinkedHashToMultiTypeMap<String>> iterator() {
        return new TsvFileIterator();
    }

    /**
     * In case we need to close early.
     * @throws IOException
     */
    public void close() throws IOException {
        textFileLineIterator.close();
    }


    /**
     * This non-static member class is the iterator implementation.
     */
    class TsvFileIterator implements Iterator<LinkedHashToMultiTypeMap<String>> {

        /**
         * Return value of next call to next().
         */
        private LinkedHashToMultiTypeMap<String> nextline;

        /**
         * The constructor for the iterator.
         */
        public TsvFileIterator() {
            // Open the file and read and remember the first line.
            // We peek ahead like this for the benefit of hasNext().
            try {
                nextline = getNextLine(true);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * This will read the next line from the file. This will return
         * null if the end of file is found. This will not return comment lines
         * (lines which start with "#").
         * @param skipOneLine skip the first line. NOTE: this will skip all comments then
         * skip the first non-commented line.
         * @return the next non comment line line
         * @throws IOException error converting data to LinkedHashToMultiTypeMap[String]
         * using tsvReader.
         */
        public LinkedHashToMultiTypeMap<String> getNextLine(
                final boolean skipOneLine) throws IOException {
            if (skipOneLine) {
                while (iterator.hasNext()) {
                    final String readLine = iterator.next();
                    if (!readLine.startsWith(COMMENT_CHAR)) {
                        break;
                    }
                }
            }
            while (iterator.hasNext()) {
                final String readLine = iterator.next();
                if (!readLine.startsWith(COMMENT_CHAR)) {
                    return tsvReader.readDataToMap(readLine);
                }
            }
            return null;
        }

        /**
         * If the next line is non-null, then we have a next line.
         * @return if we have a next line
         */
        public boolean hasNext() {
            return nextline != null;
        }

        /**
         * Return the next line, but first read the line that follows it.
         * @return the next line
         */
        public LinkedHashToMultiTypeMap<String> next() {
            try {
                final LinkedHashToMultiTypeMap<String> result = nextline;

                // If we haven't reached EOF yet
                if (nextline != null) {
                    nextline = getNextLine(false);
                }

                // Return the line we read last time through.
                return result;
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * Remove not supported.
         * The file is read-only; we don't allow lines to be removed.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
