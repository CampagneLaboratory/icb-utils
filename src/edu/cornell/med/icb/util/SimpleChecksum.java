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
public class SimpleChecksum {

    /**
     * Private constructor.
     */
    private SimpleChecksum() {
    }

    /**
     * The characters to use in the checksum.
     */
    private final static String[] CHECKSUM_CHARS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"};

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

        Adler32 adler = new Adler32();
        adler.update(valToChecksum.getBytes());
        long checksum = adler.getValue();

        // Take the hex value and use HALF of the
        // left hex digits to make the first character
        // and half of right hex digits to make the
        // second character.
        String checksumString = Long.toHexString(checksum);
        int leftSize = checksumString.length() / 2;
        int rightSize = checksumString.length() - leftSize;
        long leftMask = makeHexMask("F", leftSize, "0", rightSize);
        long rightMask = makeHexMask("0", leftSize, "F", rightSize);
        long left = (checksum & leftMask) % CHECKSUM_CHARS.length;
        long right = (checksum & rightMask) % CHECKSUM_CHARS.length;

        return valToChecksum + CHECKSUM_CHARS[(int)left] + CHECKSUM_CHARS[(int)right];
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
        String baseString = checksumString.substring(
                0, checksumString.length() - 2);

        String newVal = simpleChecksum(baseString);
        return checksumString.equals(newVal);
    }

    /**
     * Make a hex mask of leftSize leftDigits
     * and rightSize rightDigigs, such as
     * ("F", 3, "0", 4) would produce the long
     * value for 0xFFF0000.
     * @param leftDigit the digit on the left
     * @param leftSize the number of digits on the left
     * @param rightDigit the digit on the right
     * @param rightSize the number of digits on the right
     * @return the long value for the mask
     */
    private static long makeHexMask(
            final String leftDigit, final int leftSize,
            final String rightDigit, final int rightSize) {
        String mask = "";
        for (int i = 0; i < leftSize; i++) {
            mask += leftDigit;
        }
        for (int i = 0; i < rightSize; i++) {
            mask += rightDigit;
        }
        return Long.parseLong(mask, 16);
    }

}
