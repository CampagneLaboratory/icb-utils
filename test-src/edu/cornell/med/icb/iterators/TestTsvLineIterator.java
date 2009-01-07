/*
 * Copyright (C) 2008 Institute for Computational Biomedicine,
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

import edu.cornell.med.icb.io.TsvToFromMap;
import edu.cornell.med.icb.maps.LinkedHashToMultiTypeMap;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Test the tsv line iterator.
 * @author Kevin Dorff
 */
public class TestTsvLineIterator {
    /**
     * Read the tsv file where we provide the TsvToFromMap manually.
     * @throws IOException
     */
    @Test
    public void readTsvFile() throws IOException {
        final TsvToFromMap tsvReader = new TsvToFromMap("one", "two", "three");
        int lineNo = 0;
        for (final LinkedHashToMultiTypeMap<String> lines :
                new TsvLineIterator("test-input/tsv-test-file.txt", tsvReader)) {
            switch (lineNo) {
                case 0:
                    assertEquals("cat", lines.get("one"));
                    assertEquals(1.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {3.0, 4.0, 5.0}, lines.getDoubleArray("three")));
                    break;
                case 1:
                    assertEquals("dog", lines.get("one"));
                    assertEquals(2.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {6.0, 7.0, 8.0}, lines.getDoubleArray("three")));
                    break;
                case 2:
                    assertEquals("fish", lines.get("one"));
                    assertEquals(3.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {9.0, 10.0, 11.0}, lines.getDoubleArray("three")));
                    break;
            }
            lineNo++;
        }
        assertEquals(3, lineNo);
    }

    /**
     * Read the tsv file, the TsvToFromMap generated automatically from the file.
     * @throws IOException
     */
    @Test
    public void readTsvFileNoTsvToFromMap() throws IOException {
        int lineNo = 0;
        for (final LinkedHashToMultiTypeMap<String> lines :
                new TsvLineIterator("test-input/tsv-test-file.txt")) {
            switch (lineNo) {
                case 0:
                    assertEquals("cat", lines.get("one"));
                    assertEquals(1.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {3.0, 4.0, 5.0}, lines.getDoubleArray("three")));
                    break;
                case 1:
                    assertEquals("dog", lines.get("one"));
                    assertEquals(2.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {6.0, 7.0, 8.0}, lines.getDoubleArray("three")));
                    break;
                case 2:
                    assertEquals("fish", lines.get("one"));
                    assertEquals(3.0, lines.getDouble("two"));
                    assertTrue(Arrays.equals(
                            new double[] {9.0, 10.0, 11.0}, lines.getDoubleArray("three")));
                    break;
            }
            lineNo++;
        }
        assertEquals(3, lineNo);
    }

}

