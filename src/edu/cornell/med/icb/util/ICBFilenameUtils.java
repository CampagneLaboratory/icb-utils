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

import it.unimi.dsi.lang.MutableString;
import org.apache.commons.io.FilenameUtils;

/**
 * Utility methods related to filenames.
 * @author Kevin Dorff
 */
public class ICBFilenameUtils {
    /**
     * Private constructor for utility class.
     */
    private ICBFilenameUtils() {
    }

    /**
     * This works somewhat differently than Apache Commons FilenameUtils.concat(). Notably
     * it always uses unix separators, if a "parts" entry starts with a "/" that's OK,
     * it will just be stripped out. Multiple ending "/" (such as s3://) are just fine.
     * This does NOT handle "..", etc. as FilenameUtils.concat() does. Also, this takes
     * n number of parts instead of just two. If ANY of them are null, this will return
     * null. See the associated test for some examples
     * @param parts the filename parts
     * @return the path concat'd together
     */
    public static String concatPathParts(final String... parts) {
        if (parts == null || parts.length == 0) {
            return null;
        }
        int estimatedLength = parts.length;
        for (final String part : parts) {
            if (part == null) {
                return null;
            }
            estimatedLength += part.length();
        }
        final MutableString result = new MutableString(estimatedLength);
        for (int i = 0; i < parts.length; i++) {
            final String part = FilenameUtils.separatorsToUnix(parts[i]);
            if (part.length() == 0) {
                // Doesn't contribute
                continue;
            }
            if (i == 0) {
                result.append(part);
                continue;
            }
            int skipInitialChars = 0;
            while (part.charAt(skipInitialChars) == '/') {
                skipInitialChars++;
            }
            if (!result.endsWith("/")) {
                result.append('/');
            }
            result.append(part, skipInitialChars, part.length());
        }
        return result.toString();
    }
}
