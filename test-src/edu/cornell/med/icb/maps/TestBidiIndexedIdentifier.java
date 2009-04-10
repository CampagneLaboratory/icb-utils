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

package edu.cornell.med.icb.maps;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test the BidiIndexedIdentifier.
 *
 * @author Kevin Dorff
 */
public class TestBidiIndexedIdentifier {
    @Test
    public void mainTest() {
        final String zero = "zero";
        final String one = "one";
        final String two = "two";
        final String three = "three";

        BidiIndexedIdentifier indexIdent = new BidiIndexedIdentifier();
        assertEquals(0, indexIdent.getIndexForIdentifier(zero));
        assertEquals(1, indexIdent.getIndexForIdentifier(one));
        assertEquals(2, indexIdent.getIndexForIdentifier(two));
        assertEquals(3, indexIdent.getIndexForIdentifier(three));

        assertEquals(0, indexIdent.getIndexForIdentifier(zero));
        assertEquals(1, indexIdent.getIndexForIdentifier(one));
        assertEquals(2, indexIdent.getIndexForIdentifier(two));
        assertEquals(3, indexIdent.getIndexForIdentifier(three));
        assertEquals(false, indexIdent.containsIdentifier("not_there"));
        assertEquals(4, indexIdent.getIndexForIdentifier("not_there"));
        assertEquals(true, indexIdent.containsIdentifier("not_there"));

        assertEquals(zero, indexIdent.getIdentifierForIndex(0));
        assertEquals(one, indexIdent.getIdentifierForIndex(1));
        assertEquals(two, indexIdent.getIdentifierForIndex(2));
        assertEquals(null, indexIdent.getIdentifierForIndex(5));

    }
}
