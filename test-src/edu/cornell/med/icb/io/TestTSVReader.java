/*
 * Copyright (C) 2007-2010 Institute for Computational Biomedicine,
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullReader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Some simple validation on the {@link edu.cornell.med.icb.io.TSVReader}.
 */
public class TestTSVReader {
    /**
     * Test a stream has no data.
     */
    @Test
    public void testEmptyStream() throws IOException {
        final TSVReader reader = new TSVReader(new NullReader(0));
        assertFalse("Null reader should have no lines", reader.hasNext());
    }

    /**
     * Test a stream that contains no tabs.
     */
    @Test
    public void testNoTabs() throws IOException {
        final String foo =
                "The quick brown fox jumps over the lazy dog."
                + IOUtils.LINE_SEPARATOR
                + "How now brown cow"
                + IOUtils.LINE_SEPARATOR
                + "See Spot run.  Run spot run!";
        final TSVReader reader = new TSVReader(new StringReader(foo));
        assertTrue(reader.hasNext());
        reader.next();
        assertEquals("Line should have a single token", 1, reader.numTokens());
        assertEquals("The quick brown fox jumps over the lazy dog.",
                reader.getString());

        assertTrue(reader.hasNext());
        reader.next();
        assertEquals("Line should have a single token", 1, reader.numTokens());
        assertEquals("How now brown cow", reader.getString());

        assertTrue(reader.hasNext());
        reader.next();
        assertEquals("Line should have a single token", 1, reader.numTokens());
        assertEquals("See Spot run.  Run spot run!", reader.getString());

        assertFalse("Should have no lines", reader.hasNext());
    }

    /**
     * Test close functionality.
     */
    @Test
    public void testClose() throws IOException {
        final TSVReader reader = new TSVReader(new NullReader(42));
        assertTrue("There should be something here", reader.hasNext());

        reader.close();
        try {
            assertFalse("Reader should be closed", reader.hasNext());
        } catch (IOException e) {
            // this is good...
            return;
        }
        fail("TSVReader should be closed");
    }
}
