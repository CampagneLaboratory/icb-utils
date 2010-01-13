/*
 * Copyright (C) 2007-2010 Institute for Computational Biomedicine,
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

import edu.cornell.med.icb.util.ICBStringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * Helper class to parse tab/single character delimited file.
 *
 * @author Fabien Campagne
 *         Date: Apr 14, 2007
 *         Time: 2:03:33 PM
 */
public final class TSVReader implements Closeable {

    /**
     * The buffered reading being used.
     */
    private final BufferedReader bufferedReader;

    /**
     * The current line that has been read.
     */
    private String currentLine;

    /**
     * The default escape character.
     */
    private static final char ESCAPE_CHAR_DEFAULT = '\\';

    /**
     * The current line parsed into tokens.
     */
    private String[] currentTokens;

    /**
     * The current token number.
     */
    private int currentTokenIndex;

    /**
     * The line delimiter character (for tokenizing the line).
     */
    private final char delimiter;

    /**
     * The comment prefix.
     */
    private String commentPrefix;

    /**
     * The escape character.
     */
    private Character escapeChar = ESCAPE_CHAR_DEFAULT;

    /**
     * If true, the results will be unescaped.
     */
    private boolean unescapeResults = true;

    /**
     * Create a new TSVReader based on the supplied reader
     * with a tab delimiter.
     * @param reader the reader to obtain the data from
     */
    public TSVReader(final Reader reader) {
        this(reader, '\t');
    }

    /**
     * Create a new TSVReader based on the supplied reader
     * with the supplied delimiter.
     * @param reader the reader to obtain the data from
     * @param delimiterVal the delimiter used to tokenize the line of text
     */
    public TSVReader(final Reader reader, final char delimiterVal) {
        super();
        if (reader instanceof BufferedReader) {
            this.bufferedReader = (BufferedReader) reader;
        } else {
            this.bufferedReader = new BufferedReader(reader);
        }
        this.delimiter = delimiterVal;
        this.commentPrefix = "#";
    }

    /**
     * Set the comment prefix that will be used to ignore
     * commented lines.
     * @param commentPrefixVal the new comment prefix.
     * @return this TSVReader for command chaining
     */
    public TSVReader setCommentPrefix(final String commentPrefixVal) {
        if (StringUtils.isBlank(commentPrefixVal)) {
            this.commentPrefix = null;
        } else {
            this.commentPrefix = commentPrefixVal.trim();
        }
        return this;
    }

    /**
     * Get the comment prefix being used.
     * @return the comment prefix being used.
     */
    public String getCommentPrefix() {
        return this.commentPrefix;
    }

    /**
     * Set the escape character being used.
     * @param escapeCharVal the escape character being used.
     * @return this TSVReader for command chaining
     */
    public TSVReader setEscapeChar(final Character escapeCharVal) {
        this.escapeChar = escapeCharVal;
        return this;
    }

    /**
     * Get the escape character being used.
     * @return the escape character being used.
     */
    public Character getEscapeChar() {
        return this.escapeChar;
    }

    /**
     * Set the if the results should be unescaped.
     * @param unescapeResultsVal if results should be unescaped.
     * @return this TSVReader for command chaining
     */
    public TSVReader setUnescapeResults(final boolean unescapeResultsVal) {
        this.unescapeResults = unescapeResultsVal;
        return this;
    }

    /**
     * Get the if the results should be unescaped.
     * @return if results should be unescaped.
     */
    public boolean getUnescapeResults() {
        return this.unescapeResults;
    }

    /**
     * Check if the file has more lines.
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

    /**
     * Get if the current line is a comment line.
     * @return true of the current line is a comment line
     */
    public boolean isCommentLine() {
        return !StringUtils.isBlank(this.commentPrefix)
                && currentLine.startsWith(this.commentPrefix);
    }

    /**
     * Skip the current line.
     */
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

    /**
     * Get the current field as an int.
     * Advances to the next field.
     * @return the current field as an int
     */
    public int getInt() {
        ensureNextField();
        return Integer.parseInt(currentTokens[currentTokenIndex++]);
    }

    /**
     * Get the current field as a float.
     * Advances to the next field.
     * @return the current field as a float
     */
    public float getFloat() {
        ensureNextField();
        return Float.parseFloat(currentTokens[currentTokenIndex++]);
    }

    /**
     * Get the current field as a double.
     * Advances to the next field.
     * @return the current field as a double
     */
    public double getDouble() {
        ensureNextField();
        return Double.parseDouble(currentTokens[currentTokenIndex++]);
    }

    /**
     * Get the current field as a String.
     * Advances to the next field.
     * @return the current field as a String
     */
    public String getString() {
        ensureNextField();
        return currentTokens[currentTokenIndex++];
    }

    /**
     * Ensures we have a next field.
     */
    private void ensureNextField() {
        if (currentTokens == null || currentTokenIndex >= currentTokens.length) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Get the number of fields on the current line.
     * @return the number of fields on the current line
     */
    public int numTokens() {
        return currentTokens.length;
    }

    /**
     * Return true if the current line is empty.
     * @return true if the current line is empty
     */
    public boolean isEmptyLine() {
        return StringUtils.isBlank(currentLine);
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
