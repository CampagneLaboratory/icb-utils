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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Utilities to deal with version numbers of source code.
 */
public final class VersionUtils {
    /**
     * Used to log debug and informational messages.
     */
    private static final Log LOG = LogFactory.getLog(VersionUtils.class);

    /**
     * Private constructor for utility class.
     */
    private VersionUtils() {
        super();
    }

    /**
     * Gets the Implementation-Version attribute from the manifest of the jar file a class
     * is loaded from.
     * @param clazz The class to get the version for
     * @return The value of the Implementation-Version attribute or "UNKNOWN" if the
     * jar file cannot be read.
     */
    public static String getImplementationVersion(final Class<?> clazz) {
        String version;
        try {
            final String classContainer =
                    clazz.getProtectionDomain().getCodeSource().getLocation().toString();
            final URL manifestUrl = new URL("jar:" + classContainer + "!/" + JarFile.MANIFEST_NAME);
            final Manifest manifest = new Manifest(manifestUrl.openStream());
            final Attributes attributes = manifest.getMainAttributes();
            version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        } catch (Exception e) {
            // pretty much any error here is ok since we may not even have a jar to read from
            version = "UNKNOWN";
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(Attributes.Name.IMPLEMENTATION_VERSION + ": " + version);
        }
        return StringUtils.defaultString(version);
    }
}
