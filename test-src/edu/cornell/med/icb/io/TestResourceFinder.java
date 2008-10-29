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

package edu.cornell.med.icb.io;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;

/**
 * Tests for ResourceFinder.
 * @author Kevin Dorff
 */
public class TestResourceFinder {
    /**
     * Test when the resource can be found via specified "config".
     */
    @Test
    public void testResrouceFinder() {
        final ResourceFinder resrouceFinder = new ResourceFinder("config");
        final URL url =  resrouceFinder.findResource("log4j.properties.sample");
        assertNotNull(url);
    }

    /**
     * Test when the resource can be found via default "config".
     */
    @Test
    public void testResrouceFinder2() {
        // Uses the default config path
        final ResourceFinder resrouceFinder = new ResourceFinder();
        final URL url =  resrouceFinder.findResource("log4j.properties.sample");
        assertNotNull(url);
    }

    /**
     * Test when the resource can't be found.
     */
    @Test
    public void testResrouceFinder3() {
        // Really, no extra paths.
        final ResourceFinder resrouceFinder = new ResourceFinder(null);
        final URL url =  resrouceFinder.findResource("log4j.properties.sample");
        assertNull(url);
    }
}
