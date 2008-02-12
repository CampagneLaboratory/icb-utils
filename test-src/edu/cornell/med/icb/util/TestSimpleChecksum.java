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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Test the simple checksum class, also tests the
 * simple checksum classes hex mask creation. 
 */
public class TestSimpleChecksum {

    /**
     * Test the checksum class.
     * This is a normal test.
     */
    @Test
    public final void testSimpleChecksumNormal() {
        String toTry = "fish";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals("fishyl", checked);
        assertTrue(SimpleChecksum.validate(checked));
    }

    /**
     * Test the checksum class.
     * This is a short test.
     */
    @Test
    public final void testSimpleChecksumShort() {
        String toTry = "a";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals("aiu", checked);
        assertTrue(SimpleChecksum.validate(checked));
    }

    /**
     * Test the checksum class.
     * This is a short test, single space.
     */
    @Test
    public final void testSimpleChecksumSpace() {
        String toTry = " ";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals(" ih", checked);
        assertTrue(SimpleChecksum.validate(checked));
    }

    /**
     * Test the checksum class.
     * Empty string doesn't checksum.
     */
    @Test
    public final void testSimpleChecksumEmpty() {
        String toTry = "";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertTrue(checked.equals(toTry));
        assertFalse(SimpleChecksum.validate(checked));
    }

    /**
     * Test the checksum class.
     * Null string doesn't checksum.
     */
    @Test
    public final void testSimpleChecksumNull() {
        String toTry = null;
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertNull(checked);
        assertFalse(SimpleChecksum.validate(checked));
    }

    /**
     * Test the checksum class.
     * Longer string.
     */
    @Test
    public final void testSimpleChecksumLonger() {
        String toTry = "this is a somewhat longer string";
        String checked = SimpleChecksum.simpleChecksum(toTry);
        assertEquals("this is a somewhat longer stringog", checked);
        assertTrue(SimpleChecksum.validate(checked));
    }

    /**
     * Test the making hex masks.
     * Null string won't make a mask.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMakeHexMaskStringsNull() {
        SimpleChecksum.makeHexMaskStrings(null);
    }

    /**
     * Test the making hex masks.
     * Empty string won't make a mask.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMakeHexMaskStringsEmpty() {
        SimpleChecksum.makeHexMaskStrings("");
    }

    /**
     * Test the making hex masks.
     * Non-hex string won't make a mask.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMakeHexMaskStringsNonHex() {
        SimpleChecksum.makeHexMaskStrings("abcdefg");
    }

    /**
     * Test the making hex masks.
     * Longer than 16 hex digits won't make a mask.
     * This is intended for longs that are converted to hex.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMakeHexMaskStringsLong() {
        SimpleChecksum.makeHexMaskStrings("abcdefabcdefabcde");
    }

    /**
     * Test the making hex masks.
     * An even number of hex digits.
     */
    @Test
     public void testMakeHexMaskStringEvenSplit() {
        String[] masks = SimpleChecksum.makeHexMaskStrings("aBcDef");
        assertEquals("FFF000", masks[0]);
        assertEquals("000FFF", masks[1]);
     }

    /**
     * Test the making hex masks.
     * An odd number of hex digits.
     */
    @Test
    public void testMakeHexMaskStringOddSplit() {
       String[] masks = SimpleChecksum.makeHexMaskStrings("ABCDEF0");
       assertEquals("FFF0000", masks[0]);
       assertEquals("000FFFF", masks[1]);
    }

    /**
     * Test the making hex masks.
     * A single hex digit.
     */
    @Test
    public void testMakeHexMaskStringShort() {
       String[] masks = SimpleChecksum.makeHexMaskStrings("A");
       assertEquals("0", masks[0]);
       assertEquals("F", masks[1]);
    }
}