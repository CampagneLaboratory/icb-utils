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

import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

/**
 * This class allows line-by-line iteration through a text file.
 * The iterator's remove() method throws UnsupportedOperatorException.
 * If the opening of the file causes an IOException, it will be thrown as normal.
 * If during the READING of the file there is an IOException, the iterator
 * will wrap it in an IllegalArgumentExceptions. This is necessary because
 * the iterator() method is defined in the interface and cannot throw an
 * exception.
 * --
 * This class is based off of the code found at
 * http://www.java2s.com/Code/Java/Language-Basics/
 *     Javaforinforinlinebylineiterationthroughatextfile.htm.
 * See the license information below.
 */

/*
 * License for Java 1.5 'Tiger': A Developer's Notebook
 *      (O'Reilly) example package
 *
 * Java 1.5 'Tiger': A Developer's Notebook (O'Reilly)
 * by Brett McLaughlin and David Flanagan.
 * ISBN: 0-596-00738-8
 *
 * You can use the examples and the source code any way you want, but
 * please include a reference to where it comes from if you use it in
 * your own products or services. Also note that this software is
 * provided by the author "as is", with no expressed or implied warranties.
 * In no event shall the author be liable for any direct or indirect
 * damages arising in any way out of the use of this software.
 */
public class TextFileLineIterator implements Iterable<String>, Closeable {

    /** The file being read. */
    private final BufferedReader in;

    /**
     * Set to true once the input closes, either manually or automatically it won't close "in"
     * more than once.
     */
    private boolean closed = false;

    /**
     * Create the object using a filename.
     * @param filenameToRead the filename to read
     * @throws java.io.IOException error opening the file to read
     */
    public TextFileLineIterator(final String filenameToRead) throws IOException {
        this(new File(filenameToRead));
    }

    /**
     * Create the object using a file.
     * @param fileToRead the filename to read
     * @throws IOException error opening the file to read
     */
    public TextFileLineIterator(final File fileToRead) throws IOException {
        this(fileToRead.toString().toLowerCase().endsWith(".gz")
                ? new GZIPInputStream(new FileInputStream(fileToRead))
                : new FileInputStream(fileToRead));
    }

    /**
     * Create the object using an InputStream.
     * @param stream the stream for the file to read
     * @throws java.io.IOException error opening the file to read
     */
    public TextFileLineIterator(final InputStream stream) {
        in = new BufferedReader(new InputStreamReader(stream));
    }

    /**
     * This is the one method of the Iterable interface.
     * @return the iterator
     */
    public Iterator<String> iterator() {
        return new TextFileIterator();
    }

    public void close() throws IOException {
        if (!closed) {
            IOUtils.closeQuietly(in);
            closed = true;
        }
    }

    /**
     * This non-static member class is the iterator implementation.
     */
    class TextFileIterator implements Iterator<String> {

        /**
         * Return value of next call to next().
         */
        private String nextline;

        /**
         * The constructor for the iterator.
         */
        public TextFileIterator() {
            // Open the file and read and remember the first line.
            // We peek ahead like this for the benefit of hasNext().
            try {
                nextline = in.readLine();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
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
        public String next() {
            try {
                final String result = nextline;

                // If we haven't reached EOF yet
                if (nextline != null) {
                    nextline = in.readLine(); // Read another line
                    if (nextline == null) {
                        close();
                    }
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
