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

package edu.cornell.med.icb.io;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

/**
 * Tests for ResourceFinder.
 * @author Kevin Dorff
 */
public class TestResourceFinder {
    /**
     * This file will be found because it is in config and config is in the classpath
     * (thanks to ant).
     */
    @Test
    public void testResrouceFinderClasspathFileNoArgsConstructor() {
        // Config is in the class path for test
        final ResourceFinder resrouceFinder = new ResourceFinder();
        final URL url =  resrouceFinder.findResource("log4j.properties.sample");
        assertNotNull(url);
    }

    /**
     * Different constructor, multi args, but passing a null. The file will be found
     * because config is in the classpath (thanks to ant).
     */
    @Test
    public void testResrouceFinderClasspathFileNullToMultiArgsConstructor() {
        // Config is in the class path for test
        final ResourceFinder resrouceFinder = new ResourceFinder(null);
        final URL url =  resrouceFinder.findResource("log4j.properties.sample");
        assertNotNull(url);
    }

    /**
     * No args constructor that won't be about to find temp-file.txt because it is out of
     * the currect directory
     */
    @Test
    public void testResrouceFinderExistingFileNoPath() {
        // Config is in the class path for test
        final ResourceFinder resrouceFinder = new ResourceFinder();
        final URL url =  resrouceFinder.findResource("temp-file.txt");
        assertNull(url);
    }

    /**
     * Find the file out of the current directory using "file" retrieval (not class loader).
     */
    @Test
    public void testResrouceFinderExistingFileInPath() {
        // Config is in the class path for test
        final ResourceFinder resrouceFinder = new ResourceFinder("/tmp/");
        final URL url =  resrouceFinder.findResource("temp-file.txt");
        assertNotNull(url);
    }

    /**
     * Test when the resource can't be found.
     */
    @Test
    public void testResrouceExistingFileInDir() {
        // Add testsupport to search path
        final ResourceFinder resrouceFinder = new ResourceFinder("testsupport");
        final URL url =  resrouceFinder.findResource("testsupport.ipr");
        assertNotNull(url);
    }

    /**
     * Create the temp file and directory, something outside of the current
     * directory.
     * @throws IOException error making directory or file
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        makeTmpDir();
        makeTmpFile();
    }

    /**
     * Make the temp dir outside of existing directory.
     */
    private static void makeTmpDir() {
        final File tmpDir = new File("/tmp/");
        if (tmpDir.exists()) {
            return;
        }
        tmpDir.mkdir();
    }

    /**
     * Make the temp file outside of existing directory.
     * @throws IOException error making file
     */
    private static void makeTmpFile() throws IOException {
        final File tmpFile = new File("/tmp/temp-file.txt");
        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(tmpFile));
            output.write("blah");
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
