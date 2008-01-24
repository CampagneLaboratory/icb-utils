/*
 * Copyright (C) 2007-2008 Institute for Computational Biomedicine,
 *               Weill Medical College of Cornell University
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

package edu.cornell.med.icb.io;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import edu.cornell.med.icb.util.ICBStringUtils;

/**
 * Helper class to parse tab/single character delimited file.
 *
 * @author Fabien Campagne
 *         Date: Apr 14, 2007
 *         Time: 2:03:33 PM
 */
public final class TSVReader implements Closeable {
    private final BufferedReader bufferedReader;
    private String currentLine;

    private final static char ESCAPE_CHAR_DEFAULT = '\\';

    private String[] currentTokens;
    private int currentTokenIndex;
    private char delimiter;
    private String commentPrefix;
    private Character escapeChar = ESCAPE_CHAR_DEFAULT;
    private boolean unescapeResults = true;

    public TSVReader(final Reader reader) {
        this(reader, '\t');
    }

    public TSVReader(final Reader reader, final char delimiter) {
        super();
        if (reader instanceof BufferedReader) {
            this.bufferedReader = (BufferedReader) reader;
        } else {
            this.bufferedReader = new BufferedReader(reader);
        }
        this.delimiter = delimiter;
        this.commentPrefix = "#";
    }

    public TSVReader setCommentPrefix(final String commentPrefix) {
        if (StringUtils.isBlank(commentPrefix)) {
            this.commentPrefix = null;
        } else {
            this.commentPrefix = commentPrefix.trim();
        }
        return this;
    }

    public String getCommentPrefix() {
        return this.commentPrefix;
    }

    public TSVReader setEscapeChar(final Character escapeChar) {
        this.escapeChar = escapeChar;
        return this;
    }

    public Character getEscapeChar() {
        return this.escapeChar;
    }

    public TSVReader setUnescapeResults(final boolean unescapeResults) {
        this.unescapeResults = unescapeResults;
        return this;
    }

    public boolean getUnescapeResults() {
        return this.unescapeResults;
    }

    /**
     * Check if the file has more lines.
     *
     * @return true of there is another line of text
     * @throws java.io.IOException problem reading from file
     */
    public boolean hasNext() throws IOException {
        if (currentLine != null) {
            return true;
        }
        currentLine = bufferedReader.readLine();
        if (currentLine == null) {
            currentTokens = null;
            return false;
        }

        return true;
    }

    public boolean isCommentLine() {
        return !StringUtils.isBlank(this.commentPrefix) &&
                currentLine.startsWith(this.commentPrefix);
    }

    public void skip() {
        currentTokenIndex = 0;
        currentLine = null;
    }

    /**
     * Prepare the line for parsing of fields. After this method is called,
     * {@link #getInt()}, {@link #getFloat()}, {@link #getDouble()} and
     * {@link #getString()} can be called on the line to retrieve field values
     * of the appropriate type.
     */
    public void next() {
        currentTokens = ICBStringUtils.split(currentLine, delimiter, escapeChar);
        if (unescapeResults) {
            currentTokens = ICBStringUtils.unescape(currentTokens, escapeChar);
        }
        currentTokenIndex = 0;
        currentLine = null;
    }

    public int getInt() {
        ensureNextField();
        return Integer.parseInt(currentTokens[currentTokenIndex++]);
    }

    public float getFloat() {
        ensureNextField();
        return Float.parseFloat(currentTokens[currentTokenIndex++]);
    }

    public double getDouble() {
        ensureNextField();
        return Double.parseDouble(currentTokens[currentTokenIndex++]);
    }

    public String getString() {
        ensureNextField();
        return currentTokens[currentTokenIndex++];
    }

    private void ensureNextField() {
        if (currentTokens == null || currentTokenIndex >= currentTokens.length) {
            throw new NoSuchElementException();
        }
    }

    public int numTokens() {
        return currentTokens.length;
    }

    public boolean isEmptyLine() {
        return currentLine.trim().length() == 0;
    }

    /**
     * Closes this stream and releases any system resources associated with it.
     * If the stream is already closed then invoking this method has no effect.
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    public void close() throws IOException {
        bufferedReader.close();
        currentLine = null;
        currentTokens = null;
    }
}
