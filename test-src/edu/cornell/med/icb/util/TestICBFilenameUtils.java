/*
 * Copyright (C) 2009-2010 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.util;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Test ICBFilenameUtils
 *
 * @author Kevin Dorff
 */
public class TestICBFilenameUtils {
    @Test
    public void testConcatPathParts() {
        assertNull(ICBFilenameUtils.concatPathParts(null));
        assertNull(ICBFilenameUtils.concatPathParts(new String[0]));
        assertEquals("a", ICBFilenameUtils.concatPathParts("a"));
        assertEquals("a/", ICBFilenameUtils.concatPathParts("a/"));
        assertEquals("a/b", ICBFilenameUtils.concatPathParts("a", "b"));
        assertEquals("a/b", ICBFilenameUtils.concatPathParts("a/", "b"));
        assertEquals("a/b", ICBFilenameUtils.concatPathParts("a/", "b"));
        assertEquals("a/b", ICBFilenameUtils.concatPathParts("a/", "/b"));
        assertEquals("a/b/c/d", ICBFilenameUtils.concatPathParts("a", "b", "c", "d"));
        assertEquals("/a/b/c/d", ICBFilenameUtils.concatPathParts("/a/", "b/", "c/", "/d"));
        assertEquals("/a/b/d", ICBFilenameUtils.concatPathParts("/a/", "///b/", "", "/d"));
        assertEquals(null, ICBFilenameUtils.concatPathParts("/a/", "b/", null, "d"));
        assertEquals("s3://maqc3/path/to/some/file", ICBFilenameUtils.concatPathParts("s3://", "maqc3", "/path/to/some/file"));
    }
}
