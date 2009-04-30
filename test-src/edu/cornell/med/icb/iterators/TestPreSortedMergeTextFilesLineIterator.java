/*
 * Copyright (C) 2009 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.iterators;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Test the PreSortedMergeTextFilesLineIterator.
 * @author Kevin Dorff
 */
public class TestPreSortedMergeTextFilesLineIterator {

    /**
     * Try to read from multiple files, removing blank lines and removing duplicates.
     * Source files are pre-sorted, data is read in a sorted manner.
     * @throws IOException error reading
     */
    @Test
    public void textIterator() throws IOException {
        final String file0 = "a\nb\nb\nc\nc\nd\n\nk";
        final String file1 = "b\nb\ne\n\n\ng\ni\n";
        final String file2 = "a\nc\nf\ng\nj\n\n\n";

        final InputStream[] streams = new InputStream[3];
        streams[0] = new ByteArrayInputStream(file0.getBytes());
        streams[1] = new ByteArrayInputStream(file1.getBytes());
        streams[2] = new ByteArrayInputStream(file2.getBytes());

        final List<String> result = new ArrayList<String>();
        for (final String line : new PreSortedMergeTextFilesLineIterator(streams)) {
            result.add(line);
        }
        assertSame(result, "a", "b", "c", "d", "e", "f", "g", "i", "j", "k");
    }

    /**
     * Same as above, but don't skip duplicates.
     * @throws IOException error reading
     */
    @Test
    public void textIteratorNoSkipDupes() throws IOException {
        final String file0 = "a\nb\nb\nc\nc\nd\n\nk";
        final String file1 = "b\nb\ne\n\n\ng\ni\n";
        final String file2 = "a\nc\nf\ng\nj\n\n\n";

        final List<InputStream> streams = new LinkedList<InputStream>();
        streams.add(new ByteArrayInputStream(file0.getBytes()));
        streams.add(new ByteArrayInputStream(file1.getBytes()));
        streams.add(new ByteArrayInputStream(file2.getBytes()));

        final List<String> result = new ArrayList<String>();
        final PreSortedMergeTextFilesLineIterator reader =
                new PreSortedMergeTextFilesLineIterator(streams);
        reader.setSkipDuplicates(false);
        for (final String line : reader) {
            result.add(line);
        }
        assertSame(result, "a", "a", "b", "b", "b", "b", "c", "c", "c", "d", "e",
                "f", "g", "g", "i", "j", "k");
    }

    /**
     * Skip dupes, but don't skip blank lines (dupe blank lines will be skipped).
     * @throws IOException error reading
     */
    @Test
    public void textIteratorNoSkipBlanks() throws IOException {
        String filename0 = null;
        String filename1 = null;
        String filename2 = null;
        try {
            filename0 = makeFile("a\nb\nb\nc\nc\nd\n\nk", ".txt");
            filename1 = makeFile("b\nb\ne\n\n\ng\ni\n", ".txt");
            filename2 = makeFile("a\nc\nf\ng\nj\n\n\n", ".txt");

            final String[] files = new String[3];
            files[0] = filename0;
            files[1] = filename1;
            files[2] = filename2;

            final List<String> result = new ArrayList<String>();
            final PreSortedMergeTextFilesLineIterator reader =
                    new PreSortedMergeTextFilesLineIterator(files);
            reader.setSkipEmptyLines(false);
            for (final String line : reader) {
                result.add(line);
            }
            assertSame(result, "a", "b", "c", "d", "", "e", "", "f", "g", "i", "j", "", "k");
        } finally {
            new File(filename0).delete();
            new File(filename1).delete();
            new File(filename2).delete();
        }
    }

    /**
     * Do not skip dupes OR empty lines.
     * @throws IOException error reading
     */
    @Test
    public void textIteratorNoSkips() throws IOException {

        String filename0 = null;
        String filename1 = null;
        String filename2 = null;
        try {
            filename0 = makeFile("a\nb\nb\nc\nc\nd\n\nk", ".txt.gz");
            filename1 = makeFile("b\nb\ne\n\n\ng\ni\n", ".txt.gz");
            filename2 = makeFile("a\nc\nf\ng\nj\n\n\n", ".txt.gz");

            final List<String> files = new LinkedList<String>();
            files.add(filename0);
            files.add(filename1);
            files.add(filename2);

            final List<String> result = new ArrayList<String>();
            final PreSortedMergeTextFilesLineIterator reader =
                    new PreSortedMergeTextFilesLineIterator(files);
            reader.setSkipDuplicates(false);
            reader.setSkipEmptyLines(false);
            for (final String line : reader) {
                result.add(line);
            }
            assertSame(result, "a", "a", "b", "b", "b", "b", "c", "c", "c", "d", "", "e", "", "",
                    "f", "g", "g", "i", "j", "", "", "k");
        } finally {
            new File(filename0).delete();
            new File(filename1).delete();
            new File(filename2).delete();
        }
    }

    /**
     * Make sure the list (actual) matches the String.... expected.
     * @param actual the actual values
     * @param expected the expected values
     */
    private void assertSame(final List<String> actual, final String... expected) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    /**
     * Make a temprary text file with the specified contents and with give extension.
     * If the extension ends in .gz, it will be a gzip'ed text file that is created.
     * @param contents the contents of the file
     * @param extension the extension to give the temporary file
     * @return the name of the file that was created
     * @throws IOException error creating the file
     */
    private String makeFile(final String contents, final String extension) throws IOException {
        final File file = File.createTempFile("tempfile", extension);
        final PrintStream ps;
        if (extension.endsWith(".gz")) {
            ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(file)));
        } else {
            ps = new PrintStream(new FileOutputStream(file));
        }
        ps.print(contents);
        ps.close();
        return file.toString();
    }
}
