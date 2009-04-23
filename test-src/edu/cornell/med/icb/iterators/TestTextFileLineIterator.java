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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the PreSortedMergeTextFilesLineIterator.
 * @author Kevin Dorff
 */
public class TestTextFileLineIterator {

    /**
     * Try to read from multiple files, removing blank lines and removing duplicates.
     * Source files are pre-sorted, data is read in a sorted manner.
     * @throws java.io.IOException error reading
     */
    @Test
    public void textIterator() throws IOException {
        final String file = "a\nb\nb\nc\nc\nd\n\nk";

        final InputStream stream = new ByteArrayInputStream(file.getBytes());

        final List<String> result = new ArrayList<String>();
        for (final String line : new TextFileLineIterator(stream)) {
            result.add(line);
        }
        assertSame(result, "a", "b", "b", "c", "c", "d", "", "k");
    }

    /**
     * Make sure the list (actual) matches the String.... expected.
     * @param actual the actual values
     * @param expected the expected values
     */
    private void assertSame(final List<String> actual, final String... expected) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }
}
