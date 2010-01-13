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

package edu.cornell.med.icb.io;

import edu.cornell.med.icb.maps.LinkedHashToMultiTypeMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generic way to read/write TSV files given the column headers. This class uses
 * the headers and reads the data into a Map or given a map can write the data to
 * a TSV file.
 * TODO: Make it less case sensitive about case, etc. when reading/writing data?
 * @author Kevin Dorff
 */
public class TsvToFromMap {
    /** The column headers for this TSV file. */
    private final List<String> columnHeaders;

    /** The number of column headers. */
    private int numColumnHeaders;

    /**
     * If the header has been written, so it doesn't get written multiple times unless
     * you really want it to be written multiple times.
     */
    private boolean headerWritten;

    /**
     * If true, readDataToMap() will be lenient with respect to column count.
     */
    private boolean lenientColumnCount;

    /**
     * Create a new TsvToFromMap object with no columns, it is expected that the columns
     * will be added later with addColumn(...).
     */
    public TsvToFromMap() {
        this.columnHeaders = new LinkedList<String>();
        this.numColumnHeaders = columnHeaders.size();
        headerWritten = false;
        lenientColumnCount = false;
    }

    /**
     * Create a new TsvToFromMap object given an array of columnHeader labels.
     * @param columnHeaderLabels the columns of the TSV file
     */
    public TsvToFromMap(final String... columnHeaderLabels) {
        this.columnHeaders = new LinkedList<String>();
        if (columnHeaderLabels != null) {
            this.columnHeaders.addAll(Arrays.asList(columnHeaderLabels));
        }
        this.numColumnHeaders = columnHeaders.size();
        headerWritten = false;
    }

    /**
     * Add a column. If that column already exists this will do nothing.
     * @param newColumnHeader the new column header to add.
     */
    public void addColumn(final String newColumnHeader) {
        if (!columnHeaders.contains(newColumnHeader)) {
            columnHeaders.add(newColumnHeader);
            numColumnHeaders++;
        }
    }

    /**
     * Obtain a copy of the column headers. You can safely modify the list that is returned
     * from this method as you are only receiving a copy of the list of column headers.
     * @return the list of column headers
     */
    public List<String> getColumnHeaders() {
        final List<String> copy = new ArrayList<String>(columnHeaders.size());
        copy.addAll(columnHeaders);
        return copy;
    }

    /**
     * Get if the header has already been written for this object.
     * @return true if the header has already been written for this object
     */
    public boolean isHeaderWritten() {
        return this.headerWritten;
    }

    /**
     * Set if the header has already been written for this object.
     * @param headerWritten if the header has already been written for this object
     */
    public void setHeaderWritten(final boolean headerWritten) {
        this.headerWritten = headerWritten;
    }

    /**
     * Write the header. Note: If the header has already been written once for this
     * object it will not be written again, if you need to write it more than once
     * (ie, you have varied the PrintWriter for out) you need to call setHeaderWritten(...).
     * @param out the stream to write the header to
     */
    public void writeHeader(final PrintWriter out) {
        if (headerWritten) {
            return;
        }
        headerWritten = true;
        int pos = 0;
        for (final String columnHeader : columnHeaders) {
            if (pos++ > 0) {
                out.print("\t");
            }
            out.print(columnHeader);
        }
        out.println();
    }

    /**
     * Given a line of text, convert it to a Map[columnName, value] of data.
     * @param line the line of data to convert to the the Map
     * @return the Map of data
     * @throws IOException error converting the data, the number of columns found
     * in the data is incorrect given the number of columns expected in the TSV file
     */
    public LinkedHashToMultiTypeMap<String> readDataToMap(final String line) throws IOException {
        if (line.startsWith("#")) {
            return null;
        }
        final String[] parts = StringUtils.split(line, '\t');
        if ((!lenientColumnCount) && (parts.length != numColumnHeaders)) {
            throw new IOException(String.format(
                    "Line should have %d columns but has %d",
                    numColumnHeaders, parts.length));
        }
        final LinkedHashToMultiTypeMap<String> result = new LinkedHashToMultiTypeMap<String>();
        int i = 0;
        final int numActualParts = parts.length;
        for (final String columnHeader : columnHeaders) {
            if (lenientColumnCount && (i >= numActualParts)) {
                result.put(columnHeader, "");
            } else {
                result.put(columnHeader, parts[i]);
            }
            i++;
        }
        return result;
    }

    /**
     * Write the Map[columnHeader, value] of data to the PrintWriter out.
     * If the Map value for the given column is missing (null) an empty
     * value (nothing) will be written for that column. Extra entries in the
     * map will just be ignored.
     * @param out where to write the data to
     * @param data the Map of data to write
     */
    public void writeDataFromMap(
            final PrintWriter out, final Map<String, String> data) {
        int pos = 0;
        for (final String columnHeader : columnHeaders) {
            if (pos++ > 0) {
                out.print('\t');
            }
            final String dataItem = data.get(columnHeader);
            if (dataItem != null) {
                out.print(dataItem);
            }
        }
        out.println();
    }

    /**
     * Construct a TsvToFromMap based on the columns of a given
     * TSV file. This assumes that the first non-commented line contains the
     * column names.
     * @param file the type of file to make the object CsvToFromMap for
     * @return the TsvToFromMap for the given type
     * @throws IOException error reading file that was supposed to be a TSV file
     */
    public static TsvToFromMap createFromTsvFile(final File file) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                final String[] parts = StringUtils.split(line, '\t');
                return new TsvToFromMap(parts);
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
        // Empty file or all lines were comments (start with "#")
        return null;
    }

    /**
     * Retrieve the number of column headers.
     * @return the number of column headers.
     */
    public int getNumColumnHeaders() {
        return numColumnHeaders;
    }

    /**
     * When executing readDataToMap() if the input string has too many or too few
     * columns it will normally throw an exception. If lenientColumnCount is set
     * to true, it will not. You should be careful if this is on!
     * @return the value of exceptionOnTooManyFields
     */
    public boolean isLenientColumnCount() {
        return lenientColumnCount;
    }

    /**
     * When executing readDataToMap() if the input string has too many or too few
     * columns it will normally throw an exception. If lenientColumnCount is set
     * to true, it will not. You should be careful if this is on!
     * @param lenientColumnCount the new value of exceptionOnTooManyFields
     */
    public void setLenientColumnCount(final boolean lenientColumnCount) {
        this.lenientColumnCount = lenientColumnCount;
    }
}
