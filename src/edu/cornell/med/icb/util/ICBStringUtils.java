/*
 * Copyright (C) 2007-2009 Institute for Computational Biomedicine,
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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import it.unimi.dsi.lang.MutableString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kevin Dorff (Oct 25, 2007)
 */
public final class ICBStringUtils {

    /** Html character replacement for ampersand (&). */
    private static final String HTML_AMP = "&amp;";

    /** Html character replacement for single quote ('). */
    private static final String HTML_SINGLE_QUOTE = "&apos;";

    /** Html character replacement for double quote ("). */
    private static final String HTML_DOUBLE_QUOTE = "&quot;";

    /** Html character replacement for double less than (<). */
    private static final String HTML_LESS_THAN = "&lt;";

    /** Html character replacement for double greater than (>). */
    private static final String HTML_GREATER_THAN = "&gt;";

    /** XML character replacement for newline. */
    private static final String XML_NEWLINE = "&#10;";

    /** XML character replacement for carriage return. */
    private static final String XML_CR = "&#13;";


    public static int RANDOM_STRING_LENGTH_DEFAULT = 7;

    /** HTML to console string substitution map. */
    private static final Map<String, String> HTML_TO_CONSOLE_MAP;
    static {
        HTML_TO_CONSOLE_MAP = new HashMap<String, String>();
        HTML_TO_CONSOLE_MAP.put("<br/>", "\n");
        HTML_TO_CONSOLE_MAP.put("<BR/>", "\n");

        HTML_TO_CONSOLE_MAP.put("<hr/>", "--------------------------\n");
        HTML_TO_CONSOLE_MAP.put("<HR/>", "--------------------------\n");

        HTML_TO_CONSOLE_MAP.put("<ul>", "\n");
        HTML_TO_CONSOLE_MAP.put("<UL>", "\n");

        HTML_TO_CONSOLE_MAP.put("</ul>", "\n");
        HTML_TO_CONSOLE_MAP.put("</UL>", "\n");

        HTML_TO_CONSOLE_MAP.put("<li>", "\n* ");
        HTML_TO_CONSOLE_MAP.put("<LI>", "\n* ");

        HTML_TO_CONSOLE_MAP.put("</li>", "");
        HTML_TO_CONSOLE_MAP.put("</LI>", "");

        HTML_TO_CONSOLE_MAP.put("<p>", "\n\n");
        HTML_TO_CONSOLE_MAP.put("<P>", "\n\n");

        HTML_TO_CONSOLE_MAP.put("</p>", "");
        HTML_TO_CONSOLE_MAP.put("</P>", "");
    }

    /** Console to HTML string substitution map. */
    private static final Map<String, String> CONSOLE_TO_HTML_MAP;
    static {
        CONSOLE_TO_HTML_MAP = new HashMap<String, String>();
        CONSOLE_TO_HTML_MAP.put("\n".toLowerCase(), "<br/>");
        CONSOLE_TO_HTML_MAP.put("\t".toLowerCase(), "&nbsp;");
        CONSOLE_TO_HTML_MAP.put(" ".toLowerCase(), "\"&nbsp;\"&nbsp;\"&nbsp;\"&nbsp;");
    }

    /** Private constructor. */
    private ICBStringUtils() {
        super();
    }

    /**
     * Remove extra spaces. This will convert tab to a single
     * space, compress all multiple spaces until a single space
     * and then trim the string.
     * @param x the incoming string
     * @return the strip stripped of extra spaces
     */
    public static String stripExtraSpaces(final String x) {
        if (x == null || x.length() == 0) {
            return x;
        }
        return x.replaceAll("\t", " ").replaceAll(" {2,}", " ").trim();
    }

    /**
     * Fix the incoming string to be safe for HTML &lt;input&gt;, etc..
     * @param inval The incoming String to be fixed. Passing
     * in a null or an empty string will return an empty string.
     * @return String that is safe to use for Javascript strings
     */
    public static String htmlFix(final String inval) {
        if (StringUtils.isEmpty(inval)) {
            return "";
        }

        String outval = StringUtils.replace(inval, "&", HTML_AMP);
        outval = StringUtils.replace(outval, "'", HTML_SINGLE_QUOTE);
        outval = StringUtils.replace(outval, "\"", HTML_DOUBLE_QUOTE);
        outval = StringUtils.replace(outval, "<", HTML_LESS_THAN);
        outval = StringUtils.replace(outval, ">", HTML_GREATER_THAN);
        return outval;
    }

    /**
     * Fix the incoming string to be safe for HTML &lt;input&gt;, etc..
     * @param inval The incoming String to be fixed. Passing
     * in a null or an empty string will return an empty string.
     * @return String that is safe to use for Javascript strings
     */
    public static String xmlFix(final String inval) {
        if (StringUtils.isBlank(inval)) {
            return StringUtils.EMPTY;
        }
        String outval = StringUtils.replace(inval, "&", HTML_AMP);
        outval = StringUtils.replace(outval, "\"", HTML_DOUBLE_QUOTE);
        outval = StringUtils.replace(outval, "<", HTML_LESS_THAN);
        outval = StringUtils.replace(outval, ">", HTML_GREATER_THAN);
        outval = StringUtils.replace(outval, "\n", XML_NEWLINE);
        outval = StringUtils.replace(outval, "\r", XML_CR);
        return outval;
    }

    /**
     * Fix a URL, encode special characters to hex values.
     * @param toEncode url to encode
     * @return the encoded url
     */
    public static String urlFix(final String toEncode) {
        String outval = StringUtils.replace(toEncode, "%", "%25"); // Do this one FIRST

        outval = StringUtils.replace(outval, "$", "%24");
        outval = StringUtils.replace(outval, "&", "%26");
        outval = StringUtils.replace(outval, "+", "%2B");
        outval = StringUtils.replace(outval, ",", "%2C");
        outval = StringUtils.replace(outval, "/", "%2F");
        outval = StringUtils.replace(outval, ":", "%3A");
        outval = StringUtils.replace(outval, ";", "%3B");
        outval = StringUtils.replace(outval, "=", "%3D");
        outval = StringUtils.replace(outval, "?", "%3F");
        outval = StringUtils.replace(outval, "@", "%40");

        outval = StringUtils.replace(outval, " ", "%20");
        outval = StringUtils.replace(outval, "\"", "%22");
        outval = StringUtils.replace(outval, "<", "%3C");
        outval = StringUtils.replace(outval, ">", "%3E");
        outval = StringUtils.replace(outval, "#", "%23");

        outval = StringUtils.replace(outval, "{", "%7B");
        outval = StringUtils.replace(outval, "}", "%7D");
        outval = StringUtils.replace(outval, "|", "%7C");
        outval = StringUtils.replace(outval, "\\", "%5C");
        outval = StringUtils.replace(outval, "^", "%5E");
        outval = StringUtils.replace(outval, "~", "%7E");
        outval = StringUtils.replace(outval, "[", "%5B");
        outval = StringUtils.replace(outval, "]", "%5D");
        outval = StringUtils.replace(outval, "`", "%60");
        return outval;
    }

    /**
     * This method tries to append a string (mod) to the end of
     * origFilename but BEFORE the extension. A list of expected
     * extensions is provided. It is done this way to handle
     * multiple "." extensions, such as ".txt.gz" so the mod
     * goes before the entire ".txt.gz" and because it is likely
     * the extension possibilities are known. It could certainly
     * be changed so if you passed null it would just take from
     * the "." to the end of the filename as the extension.
     * @param origFilename original filename
     * @param extensions expected file extensions (checked in order)
     * @param mod the text to add to the filename, before the extension
     * @return the new filename
     */
    public static String modifyFilename(final String origFilename,
            final String[] extensions, final String mod) {
        if (StringUtils.isBlank(origFilename)) {
            return mod;
        }
        String foundExt = null;
        if (extensions != null) {
            for (final String ext : extensions) {
                if (origFilename.endsWith(ext)) {
                    foundExt = ext;
                    break;
                }
            }
        }
        if (foundExt == null) {
            // didn't find one of the expected extensions
            return origFilename + mod;
        }
        return origFilename.substring(
                0, origFilename.length() - foundExt.length())
                + mod + foundExt;
    }

    /**
     * Highlight occurrences of highlightWords within
     * toHighlight. Place the value highlightStart before
     * the word and highlightEnd after it. You must provide
     * at least one of highlightStart & highlightEnd.
     * This WILL ignore the case of highlightWords when
     * marking the string.
     * @param toHighlight the text to highlight
     * @param highlightWord the word to highlight within toHighlight
     * @param highlightStart the text to place before highlightWords
     * @param highlightEnd the text to place before highlightWords
     * @return the highlighted text
     */
    public static String highlightString(
            final String toHighlight,
            final String highlightWord,
            final String highlightStart,
            final String highlightEnd) {
        final String[] highlightWords = new String[1];
        highlightWords[0] = highlightWord;
        return highlightStrings(toHighlight, highlightWords,
                highlightStart, highlightEnd);
    }

    /**
     * Highlight occurrences of highlightWords within
     * toHighlight. Place the value highlightStart before
     * the word and highlightEnd after it. You must provide
     * at least one of highlightStart & highlightEnd.
     * This WILL ignore the case of highlightWords when
     * marking the string.
     * @param toHighlight the text to highlight
     * @param highlightWords the array of words to highlight
     * within toHighlight
     * @param highlightStart the text to place before highlightWords
     * @param highlightEnd the text to place before highlightWords
     * @return the highlighted text
     */
    public static String highlightStrings(
            final String toHighlight,
            final String[] highlightWords,
            final String highlightStart,
            final String highlightEnd) {
        if (StringUtils.isBlank(toHighlight)) {
            // Source word is empty, nothing to do.
            return toHighlight;
        }
        final String[] cleanHighlightWords = cleanStringArray(highlightWords);
        if (cleanHighlightWords == null || cleanHighlightWords.length == 0) {
            // No words to highlight, nothing to do
            return toHighlight;
        }
        for (int i = 0; i < cleanHighlightWords.length; i++) {
            cleanHighlightWords[i] = cleanHighlightWords[i].toLowerCase();
        }
        if (StringUtils.isBlank(highlightStart)
                && StringUtils.isBlank(highlightEnd)) {
            // No markup tags, nothing to do.
            return toHighlight;
        }

        final StringBuilder input = new StringBuilder(toHighlight);
        final StringBuilder inputLC = new StringBuilder(toHighlight.toLowerCase());
        final StringBuilder output = new StringBuilder();
        final StringBuilder tagStart;
        final StringBuilder tagEnd;

        if (highlightStart == null) {
            tagStart = new StringBuilder();
        } else {
            tagStart = new StringBuilder(highlightStart);
        }

        if (highlightEnd == null) {
            tagEnd = new StringBuilder();
        } else {
            tagEnd = new StringBuilder(highlightEnd);
        }

        int pos;
        int size;
        while (true) {
            if (input.length() == 0) {
                // No more text to search.
                break;
            }

            pos = -1;
            size = -1;
            for (final String toFind : cleanHighlightWords) {
                // Find the FIRST of the highlight words
                final int curPos = inputLC.indexOf(toFind);
                if (curPos != -1) {
                    // We have a match
                    if (pos == -1) {
                        // First match, keep it
                        pos = curPos;
                        size = toFind.length();
                    } else if (curPos < pos) {
                        // Earlier match that what we fond before,
                        // keep it
                        pos = curPos;
                        size = toFind.length();
                    }
                }
            }
            if (pos == -1) {
                // We're done, no more matches.
                output.append(input);
                break;
            }
            assert size > 0;
            if (pos > 0) {
                // Copy before position
                output.append(input.substring(0, pos));
            }
            // Highlight and copy the text
            output.append(tagStart);
            output.append(input.substring(pos, pos + size));
            output.append(tagEnd);

            // Remove the text from the source so we can
            // find the next occurrence.
            input.delete(0, pos + size);
            inputLC.delete(0, pos + size);
        }
        return output.toString();
    }

    /**
     * This will take a String[] and clean it,
     * removing all null or empty strings including
     * strings that would trim to empty.
     * @param strings the incoming String[]
     * @return the cleaned up String[]
     */
    public static String[] cleanStringArray(final String[] strings) {
        if (strings == null) {
            return null;
        }
        final List<String> cleanList = new LinkedList<String>();
        for (final String string : strings) {
            if (StringUtils.isNotBlank(string)) {
                cleanList.add(string);
            }
        }
        return cleanList.toArray(new String[cleanList.size()]);
    }

    /**
     * Convert milliseconds to hours, minutes seconds.
     * @param t number of milliseconds
     * @return nicely formatted value
     */
    public static String millis2hms(final long t) {
        if (t < 0) {
            return "INVALID MILLISECONDS: " + t;
        }
        if (t < 1000) {
            return t + "ms";
        }
        final long s = (t / 1000) % 60;
        final long m = ((t / 1000) / 60) % 60;
        final long h = t / (3600 * 1000);
        if (h == 0 && m == 0) {
            return s + "s";
        }
        if (h == 0) {
            return m + "m " + s + "s";
        }
        return h + "h " + m + "m " + s + "s";
    }

    /**
     * Split a string. Optionally respects escape characters.
     * If multiple splitChars are encountered they will not
     * be eaten so if splitting on ":" and "\" is the
     * escape character ":a:::b:c:\::d:" will split to
     * the String array {"", "a", "", "", "b", "c", ":", "d", ""}.
     * If splitChar and escapeChar are both the same char,
     * escapeChar will be ignored.
     * @param inval string to split.
     * @param splitChar character to split the string at
     * @param escapeChar the optional escape character so the splitChar
     * can be escaped and the string will not be split at that position
     * @return array of strings, split
     */
    public static String[] split(
            final String inval, final char splitChar,
            final Character escapeChar) {
        if (inval == null) {
            return null;
        }
        if (inval.length() == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Character escape = escapeChar;
        if (escape != null && escape == splitChar) {
            // Escape and splitChar set to same character
            // Disable the escaping
            escape = null;
        }
        final List<String> output = new LinkedList<String>();
        final int length = inval.length();
        final StringBuilder curSplit = new StringBuilder();
        boolean inEscape = false;
        for (int i = 0; i < length; i++) {
            final char curChar = inval.charAt(i);
            if (!inEscape && curChar == splitChar) {
                output.add(curSplit.toString());
                curSplit.setLength(0);
                continue;
            }
            if (escape != null) {
                if (!inEscape && curChar == escape) {
                    // Going into escape. Don't save the
                    // current (escape) character.
                    inEscape = true;
                    curSplit.append(curChar);
                    continue;
                }
                if (inEscape) {
                    inEscape = false;
                }
            }
            curSplit.append(curChar);
        }
        output.add(curSplit.toString());
        return output.toArray(new String[output.size()]);
    }

    /**
     * Unescape an array of Strings.
     * @param inval the array of Strings to unescape
     * @param escapeChar the escape character
     * @return the array of Strings, unescaped.
     */
    public static String[] unescape(final String[] inval, final Character escapeChar) {
        // if nothing was passed in, just pass back the same thing
        if (inval != null && escapeChar != null && inval.length != 0) {
            for (int i = 0; i < inval.length; i++) {
                inval[i] = unescape(inval[i], escapeChar);
            }
        }
        return inval;
    }

    /**
     * Unescape a String. Essentially, this just means that if the escape
     * character is in a String it will be removed. If the escape
     * character was '\', the string "ab\\cd\efg" would become
     * "ab\cdefg".
     * @param inval the array of strings to unescape
     * @param escapeChar the escape character
     * @return the string, unescaped
     */
    public static String unescape(final String inval, final Character escapeChar) {
        if (inval == null || escapeChar == null || inval.length() == 0) {
            // Nothing to do
            return inval;
        }

        final int length = inval.length();
        final StringBuilder curSplit = new StringBuilder();
        boolean inEscape = false;
        for (int i = 0; i < length; i++) {
            final char curChar = inval.charAt(i);
            if (!inEscape && curChar == escapeChar) {
                // Going into escape. Don't save the
                // current (escape) character.
                inEscape = true;
                continue;
            }
            if (inEscape) {
                inEscape = false;
            }
            curSplit.append(curChar);
        }

        return curSplit.toString();
    }

    /**
     * Characters to not breakup, preferably.
     */
    private static final String NO_BREAK_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789[]();:";

    /**
     * This will remove redundant whitespace then add sLineBreakString as
     * necessary to help eliminate the problem (primary seen in FireFox) where
     * long names containing no (or very little) whitespace would cause the HTML
     * table to be excessively wide. It attempts to preserve words by trying to
     * not break on within "a..z1..9" (ie words and number sequences) but
     * prefers to wrap AFTER whitespace or other characters. If a word or number
     * sequence is longer than maxLength it will split it anyway. This probably
     * shouldn't be used for editing or to store things in the database just for
     * the screen on a "view" page.
     * @param stringToBreak the string to break
     * @param maxLength the maximum length for a line
     * @param breakStr character to cause a break
     * @return the string, wrapped as necessary
     */
    public static String makeStringWrappable(final String stringToBreak,
                                             final int maxLength,
                                             final String breakStr) {
        final StringBuilder returnVal = new StringBuilder();

        // Check if there is no need to break whatsoever
        // and just return
        if (stringToBreak != null) {
            // Remove all beginning, ending, and duplicate whitespace
            String remain = stringToBreak.replaceAll("[\t|\n| ]+", " ").trim();

            if (remain.length() <= maxLength) {
                return remain;
            }

            // Intelligently add spaces to allow for nicer
            // html line breaking
            int pos = maxLength;
            while (true) {
                // Scan backwards for a non alpha-numeric.
                if (pos < 0) {
                    // We never found a char to split on.
                    // We just have to split.
                    pos = maxLength;

                    returnVal.append(remain.substring(0, pos)).append(breakStr);
                    remain = remain.substring(pos, remain.length());
                    if (remain.length() <= maxLength) {
                        // We're done. The remaining text will fit on one line
                        returnVal.append(remain);
                        break;
                    }
                    // There is more to split.
                    pos = maxLength;
                    continue;
                }

                final String curChar = remain.substring(pos, pos + 1).toLowerCase();
                if (!NO_BREAK_CHARS.contains(curChar)) {
                    // We found a non alpha-numeric char. Split after it.
                    returnVal.append(remain.substring(0, pos + 1)).append(breakStr);
                    remain = remain.substring(pos + 1, remain.length());
                    if (remain.length() <= maxLength) {
                        // We're done. The remaining text will fit on one line
                        returnVal.append(remain);
                        break;
                    }
                    // There is more to split.
                    pos = maxLength;
                    continue;
                }
                // The current character wasn't an ideal split. Let's
                // look at the previous character.
                pos--;
            }
        }

        // Return the more wrappable string.
        return returnVal.toString();
    }

    /**
     * Format a simple HTML string for the console.
     * @param html html string
     * @return console version
     */
    public static String htmlToConsole(final String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }
        String work = html;
        for (final String htmlVal : HTML_TO_CONSOLE_MAP.keySet()) {
            final String replaceVal = HTML_TO_CONSOLE_MAP.get(htmlVal);
            work = StringUtils.replace(work, htmlVal, replaceVal);
        }
        return work;
    }

    /**
     * Format a simple string with spaces, newlines, tabs to simple html.
     * @param console console value
     * @return html value
     */
    public static String consoleToHtml(final String console) {
        if (StringUtils.isBlank(console)) {
            return "";
        }
        String work = console;
        for (final String consoleVal : CONSOLE_TO_HTML_MAP.keySet()) {
            final String replaceVal = HTML_TO_CONSOLE_MAP.get(consoleVal);
            work = StringUtils.replace(work, consoleVal, replaceVal);
        }
        return work;
    }

    /**
     * Generate a random string of characters A..Z of length RANDOM_STRING_LENGTH_DEFAULT.
     * @return the random string of characters
     */
    public static String generateRandomString() {
        return generateRandomString(RANDOM_STRING_LENGTH_DEFAULT);
    }

    /**
     * Generate a random string of characters A..Z of length 'length'.
     * @param length the lengt of the string to generate.
     * @return the random string of characters
     */
    public static String generateRandomString(final int length) {
        return RandomStringUtils.random(length, true, false).toUpperCase();
    }

    /**
     * Fixes filenames to be "safe" removing unwanted characters.
     * Note that this isn't for paths, just for filenames.
     * Spaces will be replaced with "-". Characters other than
     * A-Z, a-z, 0-9, and non _, -, and . will be replaced with _.
     * "_" will be substituted for unwanted characters.
     * @param inval incoming filename (not a full path)
     * @return safer filename
     */
    public static String safeFilename(final String inval) {
        if (StringUtils.isBlank(inval)) {
            return StringUtils.EMPTY;
        }
        String outval = inval.trim();
        outval = StringUtils.replace(outval, " ", "-");
        while (outval.contains("--")) {
            outval = StringUtils.replace(outval, "--", "-");
        }
        outval = outval.replaceAll("[^a-zA-Z0-9\\-._]", "_");
        while (outval.contains("__")) {
            outval = StringUtils.replace(outval, "__", "_");
        }
        return outval;
    }

    /**
     * Make byte sizes (such as file or memory sizes) human readable, such as
     * 42,462,614 becomes 40.5 MB. This mehtod assumes 1KB = 1024B.
     * @param bytes the number of bytes to create a human readable size for.
     * @return human readable string
     */
    public static String humanMemorySize(long bytes) {
        return humanMemorySize(bytes, 1024);
    }

    /**
     * Make byte sizes (such as file or memory sizes) human readable, such as
     * 42,462,614 becomes 40.5 MB. Normally for memory 1KB = 1024B but by
     * adjusting the unit parameter you can set this to 1000 or whatever you like.
     * @param bytes the number of bytes to create a human readable size for.
     * @param unit normally one would supply 1024 (1KB = 1024B) but this can be
     * changed to 1000 or whatever makes sense for your program.
     * @return human readable string
     */
    public static String humanMemorySize(long bytes, int unit) {
        MutableString result = new MutableString();
        if (bytes < unit) {
            result.append(bytes);
            result.append(" B");
        } else {
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            result.append(String.format("%.1f", bytes / Math.pow(unit, exp)));
            if (result.endsWith(".0")) {
                // If the result ends in .0, strip it off
                result.length(result.length() - 2);
            }
            result.append(' ');
            result.append(("KMGTPE").charAt(exp - 1));
            result.append('B');
        }
        return result.toString();
    }
}
