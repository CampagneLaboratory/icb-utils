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

package edu.cornell.med.icb.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA. User: Kevin Dorff Date: Oct 25, 2007 Time: 2:50:35 PM To change this
 * template use File | Settings | File Templates.
 */
public class TestSimpleChecksum extends TestCase {

    /**
     * Test the checksum class.
     */
    public final void testSimpleChecksum() {
        String toTry = "fish";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertFalse(checked.equals(toTry));
        assertTrue(SimpleChecksum.validate(checked));

        toTry = "a";
        checked = SimpleChecksum.simpleChecksum(toTry);
        assertFalse(checked.equals(toTry));
        assertEquals("aIU", checked);
        assertTrue(SimpleChecksum.validate(checked));

        toTry = " ";
        checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals(" IH", checked);
        assertFalse(checked.equals(toTry));
        assertTrue(SimpleChecksum.validate(checked));

        toTry = "";
        checked = SimpleChecksum.simpleChecksum(toTry);
        assertTrue(checked.equals(toTry));
        assertFalse(SimpleChecksum.validate(checked));

        toTry = null;
        checked = SimpleChecksum.simpleChecksum(toTry);
        assertNull(checked);
        assertFalse(SimpleChecksum.validate(checked));

        toTry = "this is a somewhat longer string";
        checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals("this is a somewhat longer stringOG", checked);
        assertFalse(checked.equals(toTry));
        assertTrue(SimpleChecksum.validate(checked));
    }

}