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
import org.apache.commons.lang.SystemUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Validates functionality of {@link VersionUtils}.
 */
public class TestVersionUtils {
    /**
     * Default constructor.
     */
    public TestVersionUtils() {
        super();
    }

    /**
     * Validates functionality of {@link VersionUtils#getImplementationVersion(Class<?> )}.
     */
    @Test
    public void getObjectImplementationVersion() {
        final String version = VersionUtils.getImplementationVersion(SystemUtils.class);
        assertNotNull("Version number should never be null", version);
        assertTrue("Version number should be at least 2.3", Double.parseDouble(version) >= 2.3);
    }

    /**
     * Validates functionality of {@link VersionUtils#getImplementationVersion(Class<?> )}
     * with a class that has an unknown version.
     */
    @Test
    public void getUnknownImplementationVersion() {
        // this should give us unknown since there should be no jar for tests
        final String version = VersionUtils.getImplementationVersion(this.getClass());
        assertNotNull("Version number should never be null", version);
        assertEquals("Version number should be unknown", "UNKNOWN", version);
    }

    /**
     * Validates functionality of {@link VersionUtils#getImplementationVersion(Class<?> )}
     * with a null class.
     */
    @Test
    public void getNullImplementationVersion() {
        final String version = VersionUtils.getImplementationVersion(null);
        assertNotNull("Version number should never be null", version);
        assertEquals("Version number should be unknown", "UNKNOWN", version);
    }

    /**
     * Validates functionality of {@link VersionUtils#getImplementationVersion(Class<?> )}
     * with a jar is readable but that has no implementation version attribute.
     */
    @Test
    public void getNoImplementationVersion() {
        final String version = VersionUtils.getImplementationVersion(org.junit.Test.class);
        assertNotNull("Version number should never be null", version);
        assertEquals("Version number should be empty", StringUtils.EMPTY, version);
    }

}
