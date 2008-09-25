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

import org.apache.unicode.UnicodeCharUtil;
import com.ibm.icu.text.Transliterator;

/**
 * Class for normalizing strings, such as removing accents.
 * @author Kevin Dorff
 */
public class ICBStringNormalizer {

    /** Private constructor. */
    private ICBStringNormalizer() {
        super();
    }

    /** Helper object to remove accents using ICU4J. */
    private static Transliterator accentsRemover = Transliterator
            .getInstance("NFD; [:M:] Remove; NFC;");

    /**
     * Take a string with (international) access and return the same
     * string but with the accents removed. This currently requires
     * Java 1.6 and will possibly given compilation warnings such as
     * "warning: sun.text.Normalizer is Sun proprietary API and may
     * be removed in a future release". If this happens, ICU4J can be
     * used to remove accents, but requires a 5MB jar... this is a much
     * more compact way of doing it.
     * @param accentedValue the string that may have accents
     * @return the string without the accents.
     */
    public static String removeAccents(final String accentedValue) {
        String noAccents = accentsRemover.transliterate(accentedValue);
        final int length = noAccents.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char curChar = noAccents.charAt(i);
            if (UnicodeCharUtil.isCombiningCharacter(curChar)) {
                continue;
            } else if (UnicodeCharUtil.isSpacingModifier(curChar)) {
                continue;
            }
            final char foldC = UnicodeCharUtil.foldNonDiacriticChar(curChar);
            if (foldC == 0x00) {
                result.append(curChar);
            } else {
                result.append(foldC);
            }
        }
        return result.toString();
    }
}
