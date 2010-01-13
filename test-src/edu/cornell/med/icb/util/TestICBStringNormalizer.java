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

package edu.cornell.med.icb.util;

import junit.framework.TestCase;

/**
 * Test the ICBNormalizer class.
 * @author Kevin Dorff
 */
public class TestICBStringNormalizer extends TestCase {

    /**
     * Since the server doesn't support the below test, I've added this one as a placeholder.
     */
    public void testStub() {
    }

    /** The server doesn't like the accented characters to be in the java strings here. */
    /**
    public void testRemoveAccents() {
        assertEquals("Stuve", ICBStringNormalizer.removeAccents("St�ve"));
        assertEquals("Biologia", ICBStringNormalizer.removeAccents("Biolog�a"));
        assertEquals("Anabalon R", ICBStringNormalizer.removeAccents("Anabal�n R"));
        assertEquals("patron de metilacion genico",
                ICBStringNormalizer.removeAccents("patr�n de metilaci�n g�nico"));
        assertEquals("Investigacao", ICBStringNormalizer.removeAccents("Investiga��o"));
    }
    */
}
