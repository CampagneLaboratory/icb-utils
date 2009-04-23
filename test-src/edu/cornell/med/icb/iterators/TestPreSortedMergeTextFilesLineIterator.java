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
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the PreSortedMergeTextFilesLineIterator.
 * @author Kevin Dorff
 */
public class TestPreSortedMergeTextFilesLineIterator {

    /**
     * Try to read from multiple files, removing blank lines and removing duplicates.
     * Source files are pre-sorted, data is read in a sorted manner.
     * @throws IOException error reading
     */
    @Test
    public void textIterator() throws IOException {
        final String file0 = "a\nb\nb\nc\nc\nd\n\nk";
        final String file1 = "b\nb\ne\n\n\ng\ni\n";
        final String file2 = "a\nc\nf\ng\nj\n\n\n";

        final InputStream[] streams = new InputStream[3];
        streams[0] = new ByteArrayInputStream(file0.getBytes());
        streams[1] = new ByteArrayInputStream(file1.getBytes());
        streams[2] = new ByteArrayInputStream(file2.getBytes());

        final List<String> result = new ArrayList<String>();
        for (final String line : new PreSortedMergeTextFilesLineIterator(streams)) {
            result.add(line);
        }
        assertSame(result, "a", "b", "c", "d", "e", "f", "g", "i", "j", "k");
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
