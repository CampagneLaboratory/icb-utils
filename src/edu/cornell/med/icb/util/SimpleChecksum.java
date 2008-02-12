/*
 * Copyright (C) 2008 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.util;

import org.apache.commons.lang.StringUtils;

import java.util.zip.Adler32;

/**
 * Utility class to add a simple 2 character checksum
 * to a string and a method to validate the string has
 * the correct checksum.
 * @author Kevin Dorff
 */
public final class SimpleChecksum {

    /**
     * Private constructor for utility class.
     */
    private SimpleChecksum() {
    }

    /**
     * The characters to use in the checksum.
     */
    private static final char[] CHECKSUM_CHARS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * Takes a string and returns a that same string
     * with two additional characeters which are a simple
     * checksum, each char will be "A".."Z". An empty
     * (no characters) or null string will just be
     * returned with no change.
     * @param valToChecksum the string to add the checksum to
     * @return the string with the checksum
     */
    public static String simpleChecksum(final String valToChecksum) {
        if (StringUtils.isEmpty(valToChecksum)) {
            return valToChecksum;
        }

        final Adler32 adler = new Adler32();
        adler.update(valToChecksum.getBytes());
        final long[] result = splitLong(adler.getValue());

        return String.format("%s%c%c", valToChecksum,
                CHECKSUM_CHARS[(int) result[0]],
                CHECKSUM_CHARS[(int) result[1]]);
    }

    /**
     * Take a string that contains a simple checksum and
     * validate the that checksum is correct.
     * @param checksumString the string to check where the
     * last two characters are the checksum
     * @return true of the checksum is correct
     */
    public static boolean validate(final String checksumString) {
        if ((checksumString == null) || (checksumString.length() < 3)) {
            return false;
        }
        final String baseString = checksumString.substring(
                0, checksumString.length() - 2);

        final String newVal = simpleChecksum(baseString);
        return checksumString.equals(newVal);
    }

    /**
     * Splits a long value into two long values, representing
     * the left and right side. This isn't necessarily the
     * top four bytes and bottom four bytes, it is based on
     * the number of bytes that are actually used in the value.
     * @param inval long value to split
     * @return two resultant long values in an array
     */
    private static long[] splitLong(final long inval) {
        // Take the hex value and use HALF of the
        // left hex digits to make the first character
        // and half of right hex digits to make the
        // second character.
        final String checksumString = Long.toHexString(inval);
        final String[] maskStrings = makeHexMaskStrings(checksumString);

        final long[] maskLongs = new long[2];
        maskLongs[0] = Long.parseLong(maskStrings[0], 16);
        maskLongs[1] = Long.parseLong(maskStrings[1], 16);

        final long[] result = new long[2];
        result[0] = (inval & maskLongs[0]) % CHECKSUM_CHARS.length;
        result[1] = (inval & maskLongs[1]) % CHECKSUM_CHARS.length;
        return result;
    }

    /**
     * Make two hex masks of the incoming hex string. The hex string must be
     * no longer than 16 digits, not be null/empty, and (of course) contain only
     * hex digits. The two masks will to mask the left and right side of
     * the string. If you pass in "abcdef" you will receive back
     * "fff000" and "000fff".
     * @param hexString hex string to make two masks for
     * @return two hex mask strings in a String[]
     */
    static String[] makeHexMaskStrings(
            final String hexString) {

        if (StringUtils.isEmpty(hexString)) {
            throw new IllegalArgumentException("Hex string must be 1 to 16 characters long.");
        }

        if (hexString.length() > 16) {
            throw new IllegalArgumentException("Hex string must be 1 to 16 characters long.");
        }

        for (int i = 0; i < hexString.length(); i++) {
            final char c = hexString.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                continue;
            }
            if ((c >= 'A') && (c <= 'F')) {
                continue;
            }
            if ((c >= 'a') && (c <= 'f')) {
                continue;
            }
            throw new IllegalArgumentException("Hex string must contain only hex digits, 0-9A-F");
        }

        int leftSize;
        int rightSize;
        if (hexString.length() == 1) {
            leftSize = 0;
            rightSize = 1;
        } else {
            leftSize = hexString.length() / 2;
            rightSize = hexString.length() - leftSize;
        }

        final StringBuffer leftMask = new StringBuffer();
        final StringBuffer rightMask = new StringBuffer();
        for (int i = 0; i < leftSize; i++) {
            leftMask.append("F");
            rightMask.append("0");
        }
        for (int i = 0; i < rightSize; i++) {
            leftMask.append("0");
            rightMask.append("F");
        }

        final String[] masks = new String[2];
        masks[0] = leftMask.toString();
        masks[1] = rightMask.toString();
        return masks;
    }

}
