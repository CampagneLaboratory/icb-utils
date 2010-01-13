/*
 * Copyright (C) 2007-2010 Institute for Computational Biomedicine,
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
import org.apache.commons.lang.ArrayUtils;

/**
 * Created by IntelliJ IDEA. User: Kevin Dorff Date: Oct 25, 2007 Time: 2:50:35 PM To change this
 * template use File | Settings | File Templates.
 */
public class TestICBStringUtils extends TestCase {

    /** Unsafe HTML string before fixing. */
    private static final String HTMLUNSAFE =
        "<this is a \"test\" & so is 'this' >";

    /** Unsafe HTML string after fixing. */
    private static final String HTMLSAFE =
        "&lt;this is a &quot;test&quot; &amp; so is &apos;this&apos; &gt;";

    /**
     * Test strip extra spaces
     */
    public final void testStripExtraSpaces() {
        assertNull(ICBStringUtils.stripExtraSpaces(null));
        assertEquals("", ICBStringUtils.stripExtraSpaces(""));
        assertEquals("", ICBStringUtils.stripExtraSpaces(" "));
        assertEquals("", ICBStringUtils.stripExtraSpaces(" \t "));
        assertEquals("this and that",
                ICBStringUtils.stripExtraSpaces(" \t    this  and\tthat   \t   "));
    }

    /**
     * Test fix for HTML, blank.
     */
    public final void testFixHtmlBlank() {
        assertEquals(
            "Fixing for HTML (blank) did not occur correctly", "",
            ICBStringUtils.htmlFix(""));
    }

    /**
     * Test fix for HTML, null.
     */
    public final void testFixHtmlNull() {
        assertEquals(
            "Fixing for HTML (null) did not occur correctly", "",
            ICBStringUtils.htmlFix(null));
    }

    /**
     * Test fix for HTML, null.
     */
    public final void testFixHtml() {
        assertEquals(
            "Fixing for HTML did not occur correctly", HTMLSAFE,
            ICBStringUtils.htmlFix(HTMLUNSAFE));
    }

    public final void testModifyFilename() {
        final String[] exts = {".txt", ".gz"};
        assertNull(ICBStringUtils.modifyFilename(null, null, null));
        assertEquals("-test", ICBStringUtils.modifyFilename(null, null, "-test"));
        assertEquals("TEST-test", ICBStringUtils.modifyFilename("TEST", null, "-test"));
        assertEquals("TEST-test", ICBStringUtils.modifyFilename("TEST", exts, "-test"));
        assertEquals("TEST-test.gz", ICBStringUtils.modifyFilename("TEST.gz", exts, "-test"));
        assertEquals("TEST-test.txt", ICBStringUtils.modifyFilename("TEST.txt", exts, "-test"));
        assertEquals("TEST.dat-test", ICBStringUtils.modifyFilename("TEST.dat", exts, "-test"));
    }

    public void testHighlightStrings() {
        final String[] toFind = {"cat", "hat"};
        assertEquals("[Cat] eat [Hat]",
                ICBStringUtils.highlightStrings("Cat eat Hat", toFind, "[", "]"));
        assertEquals("[Cat]s eat [Hat]s",
                ICBStringUtils.highlightStrings("Cats eat Hats", toFind, "[", "]"));
        assertEquals("[Hat] eat [Cat]",
                ICBStringUtils.highlightStrings("Hat eat Cat", toFind, "[", "]"));
        assertEquals("[Hat]s eat [Cat]s",
                ICBStringUtils.highlightStrings("Hats eat Cats", toFind, "[", "]"));
        assertEquals("[Hat] eat [Cat]",
                ICBStringUtils.highlightStrings("Hat eat Cat", toFind, "[", "]"));
        assertEquals("See the [CAT]S eat the [hat]s cold.",
                ICBStringUtils.highlightStrings("See the CATS eat the hats cold.", toFind, "[", "]"));
        assertNull(ICBStringUtils.highlightStrings(null, toFind, "[", "]"));
        assertEquals("cat", ICBStringUtils.highlightStrings("cat", null, "[", "]"));
        assertEquals("cat", ICBStringUtils.highlightStrings("cat", null, null, null));
        assertEquals("cat", ICBStringUtils.highlightStrings("cat", toFind, null, null));
        assertEquals("[cat", ICBStringUtils.highlightStrings("cat", toFind, "[", null));
        assertEquals("cat]", ICBStringUtils.highlightStrings("cat", toFind, null, "]"));
        assertEquals("see the cat]s", ICBStringUtils.highlightStrings("see the cats", toFind, null, "]"));
        assertEquals("see the [cats", ICBStringUtils.highlightStrings("see the cats", toFind, "[", null));

        assertEquals("cat", ICBStringUtils.highlightStrings("cat", null, "", ""));
        assertEquals("cat", ICBStringUtils.highlightStrings("cat", toFind, "", ""));
        assertEquals("[cat", ICBStringUtils.highlightStrings("cat", toFind, "[", ""));
        assertEquals("cat]", ICBStringUtils.highlightStrings("cat", toFind, "", "]"));
        assertEquals("see the cat]s", ICBStringUtils.highlightStrings("see the cats", toFind, "", "]"));
        assertEquals("[Cats see [cats and the [Hats", ICBStringUtils.highlightStrings("Cats see cats and the Hats", toFind, "[", ""));

        assertEquals("[Cat]s see [cat]s and the Hats", ICBStringUtils.highlightString("Cats see cats and the Hats", "cat", "[", "]"));
    }

    public void testSplitNormal() {
        checkStringArray("testSplitNormal", new String[] {"", "a", "", "", "b", "c", "\\:", "d", ""},
                ICBStringUtils.split(":a:::b:c:\\::d:", ':', '\\'));
    }
    public void testSplitNoEscapeChar() {
        // null for escape char
        checkStringArray("testSplitNoEscapeChar", new String[] {"", "a", "", "", "b", "c", "\\", "", "d", ""},
                ICBStringUtils.split(":a:::b:c:\\::d:", ':', null));
    }
    public void testSplitBadEscapeChar() {
        // Same escape and split char, escape char will be ignored
        checkStringArray("testSplitBadEscapeChar", new String[] {"", "a", "", "", "b", "c", "\\", "", "d", ""},
                ICBStringUtils.split(":a:::b:c:\\::d:", ':', ':'));
    }

    public void testSplitNull() {
        // Same escape and split char, escape char will be ignored
        assertNull(ICBStringUtils.split(null, ':', ' '));
    }

    public void testSplitEmpty() {
        checkStringArray("testSplitEmpty", ArrayUtils.EMPTY_STRING_ARRAY, ICBStringUtils.split("", ':', ' '));
    }

    public void testSingleCharString() {
        checkStringArray("testSingleCharString", new String[] {" "}, ICBStringUtils.split(" ", ':', '\\'));
    }

    public void testSingleWordString() {
        checkStringArray("testSingleWordString", new String[] {"This is a test"}, ICBStringUtils.split("This is a test", ':', ':'));
    }

    public void testMultiWordWithExtra() {
        checkStringArray("testMultiWordWithExtra", new String[] {"This", "is", "a", "", "test"}, ICBStringUtils.split("This is a  test", ' ', null));
    }

    public void testAllSplits() {
        checkStringArray("testMultiWordWithExtra", new String[] {"", "", "", ""}, ICBStringUtils.split(":::", ':', null));
    }

    public void testUnescapeNull() {
        assertNull(ICBStringUtils.unescape((String)null, ':'));
    }

    public void testNoEscapeChar() {
        assertEquals("this is\\a test", ICBStringUtils.unescape("this is\\a test", null));
    }

    public void testUnescapeVariousEscapes() {
        assertEquals(":\\:", ICBStringUtils.unescape("\\:\\\\:", '\\'));
    }

    public void testMillis2hms() {
        assertEquals("0ms", ICBStringUtils.millis2hms(0));
        assertEquals("500ms", ICBStringUtils.millis2hms(500));
        assertEquals("999ms", ICBStringUtils.millis2hms(999));
        assertEquals("1s", ICBStringUtils.millis2hms(1000));
        assertEquals("1s", ICBStringUtils.millis2hms(1500));
        assertEquals("3s", ICBStringUtils.millis2hms(3520));
        assertEquals("15m 13s", ICBStringUtils.millis2hms((15*60*1000) + (13*1000)));
        assertEquals("2h 14m 8s", ICBStringUtils.millis2hms((2*60*60*1000) + (14*60*1000) + (8*1000)));
    }

    private void checkStringArray(final String testname, final String[] expected, final String[] found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Test: " + testname + ". Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.length != found.length) {
            fail("Test: " + testname + ". Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (!(expected[i].equals(found[i]))) {
                fail("Test: " + testname + ". Arrays differ. Expected was " + ArrayUtils.toString(expected)
                        + " and found was " + ArrayUtils.toString(found));
                return;
            }
        }
    }

    public void testBreakString() {
        assertEquals("string to <br/>break",
                ICBStringUtils.makeStringWrappable(
                        "string to break", 12, "<br/>"));
    }

    public void testBreakStringNoBreak() {
        assertEquals("string to break",
                ICBStringUtils.makeStringWrappable(
                        "string to break", 20, "<br/>"));
    }

    public void testBreakStringForceBreak() {
        assertEquals("stringtobrea<br/>k",
                ICBStringUtils.makeStringWrappable(
                        "stringtobreak", 12, "<br/>"));
    }

    public void testBreakStringMultiBreaks() {
        assertEquals(
                "this is a <br/>longer string. <br/>with "
                        + "multiplte <br/>breaks! fun eh?",
                ICBStringUtils.makeStringWrappable(
                    "this is a longer string. with "
                            + "multiplte breaks! fun eh?",
                        15, "<br/>"));
    }

    public void testGenerateRandom() {
        assertFalse(ICBStringUtils.generateRandomString().equals(ICBStringUtils.generateRandomString()));
        assertFalse(ICBStringUtils.generateRandomString(25).equals(ICBStringUtils.generateRandomString(25)));
    }
}
