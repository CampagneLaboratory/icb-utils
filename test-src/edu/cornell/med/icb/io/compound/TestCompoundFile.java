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

package edu.cornell.med.icb.io.compound;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.File;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.IntegerForLoop;

/**
 * Test the compound file reader / writer.
 * @author Kevin Dorff
 */
public class TestCompoundFile {

    /**
     * This is a general test. Write some files, read some files, delete some files.
     * Close it, add some more.
     * @throws IOException problem reading/writing
     * @throws ClassNotFoundException error deserializing data from the file
     */
    @Test
    public void testCompoundFile() throws IOException, ClassNotFoundException {
        new File("test-data/CompoundFile.dat").delete();
        CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile.dat");
        CompoundFileReader cfr = cfw.getCompoundFileReader();
        CompoundDataOutput output = cfw.addFile("file1");
        output.writeUTF("File 1 string");
        output.writeLong(45);
        output.writeUTF("File 1 string B");
        output.writeObject("File 1 StringC serialized");
        output.close();
        assertEquals(1, cfr.getFileNames().size());

        output = cfw.addFile("file2");
        output.writeUTF("File 2 string");
        output.writeLong(35);
        output.writeLong(54);
        output.writeObject("File 2 String serialized");
        output.close();
        assertEquals(2, cfr.getFileNames().size());

        cfw.close();

        cfw = new CompoundFileWriter("test-data/CompoundFile.dat");
        cfr = cfw.getCompoundFileReader();
        output = cfw.addFile("file3");
        output.writeUTF("File 3 string");
        output.writeDouble(3.14159);
        output.close();
        assertEquals(3, cfr.getFileNames().size());

        final Set<String> files = cfr.getFileNames();
        for (final String file : files) {
            System.out.println("Compound file contains file named " + file);
        }
        CompoundDataInput input = cfr.readFile("file1");
        assertEquals("File 1 string", input.readUTF());
        assertEquals(45, input.readLong());
        assertEquals("File 1 string B", input.readUTF());
        assertEquals("File 1 StringC serialized", input.readObject());
        input.close();

        cfw.deleteFile("file1");
        assertEquals(2, cfr.getFileNames().size());
        output = cfw.addFile("file1");
        output.writeUTF("File 1b string");
        output.writeDouble(2.73);
        output.close();
        assertEquals(3, cfr.getFileNames().size());

        input = cfr.readFile("file1");
        assertEquals("File 1b string", input.readUTF());
        assertEquals(2.73, input.readDouble(), 0.001);
        input.close();

        input = cfr.readFile("file2");
        assertEquals("File 2 string", input.readUTF());
        assertEquals(35, input.readLong());
        assertEquals(54, input.readLong());
        assertEquals("File 2 String serialized", input.readObject());
        input.close();

        input = cfr.readFile("file3");
        assertEquals("File 3 string", input.readUTF());
        assertEquals(3.14159, input.readDouble(), 0.001);
        input.close();

        cfr.close();
        cfw.close();
    }

    /**
     * This writes a bunch of small files and verifies the
     * data comes back from the files.
     * @throws IOException problem reading/writing
     */
    @Test
    public void testLotsOfSmallFiles() throws IOException {
        final int numFiles = 20000;
        System.out.println("Testing lots of small files");
        new File("test-data/CompoundFile2.dat").delete();
        final CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile2.dat");
        CompoundFileReader cfr = cfw.getCompoundFileReader();
        for (int x = 0; x < numFiles; x++) {
            final CompoundDataOutput output = cfw.addFile("file" + x);
            output.writeUTF("Data for file " + x);
            output.close();
            if (x % 500 == 0) {
            //     System.out.println("Loaded " + x + " files");
                assertEquals(x + 1, cfr.getFileNames().size());
            }
        }
        cfw.close();

        cfr = new CompoundFileReader("test-data/CompoundFile2.dat");
        long startTime = System.currentTimeMillis();
        for (int x = 0; x < numFiles; x++) {
            final CompoundDataInput input = cfr.readFile("file" + x);
            assertEquals("Data for file " + x, input.readUTF());
            input.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to read " + numFiles + " files " + (endTime - startTime));
        cfr.close();
    }

    /**
     * This writes objects (specifically a Long->String map)
     * several times to a file and then reads them back
     * to verify the object is serialized-deserialized correctly.
     * @throws IOException problem reading/writing
     * @throws ClassNotFoundException problem deserializing
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMapWriting() throws IOException, ClassNotFoundException {
        Map<Long, String> map = new HashMap<Long, String>();
        map.put(0L, "zero");
        map.put(1L, "one");
        map.put(2L, "two");
        map.put(3L, "three");
        map.put(4L, "four");
        map.put(5L, "five");
        map.put(6L, "six");
        map.put(7L, "seven");
        map.put(8L, "eight");
        map.put(9L, "nine");
        map.put(10L, "ten");
        map.put(11L, "eleven");
        map.put(12L, "twelve");
        map.put(13L, "thirteen");
        map.put(14L, "fourteen");
        map.put(15L, "fifteen");
        map.put(16L, "sixteen");
        map.put(17L, "seventeen");
        map.put(18L, "eighteen");
        map.put(19L, "nineteen");
        map.put(20L, "twenty");
        map.put(21L, "twenty-one");
        map.put(22L, "twenty-two");
        map.put(23L, "twenty-three");
        map.put(24L, "twenty-four");
        map.put(25L, "twenty-five");
        map.put(26L, "twenty-six");
        map.put(27L, "twenty-seven");
        map.put(28L, "twenty-eight");
        map.put(29L, "twenty-nine");
        map.put(30L, "thirty");
        map.put(40L, "forty");
        map.put(50L, "fifty");
        map.put(60L, "sixty");
        map.put(70L, "seventy");
        map.put(80L, "eighty");
        map.put(90L, "ninety");
        map.put(100L, "one hundred");
        map.put(101L, "a hundred and one");
        map.put(110L, "a hundred and ten");
        map.put(120L, "a hundred and twenty");
        map.put(200L, "two hundred");
        map.put(1000L, "one thousand");
        map.put(1001L, "a thousand and one");
        map.put(1010L, "a thousand and ten");
        map.put(2000L, "two thousand");
        map.put(10000L, "ten thousand");
        map.put(11000L, "eleven thousand");
        map.put(100000L, "one hundred thousand");
        map.put(1000000L, "one million");
        map.put(2000000L, "two million");
        map.put(1000000000L, "one billion");

        new File("test-data/CompoundFile3.dat").delete();
        CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile3.dat");
        CompoundDataOutput output = cfw.addFile("mapfile");
        output.writeObject(map);
        output.writeObject(map);
        output.writeObject(map);
        output.close();
        cfw.close();

        CompoundFileReader cfr = new CompoundFileReader("test-data/CompoundFile3.dat");
        CompoundDataInput input = cfr.readFile("mapfile");
        Map<Long, String> mapRead = (Map<Long, String>) input.readObject();
        Map<Long, String> mapRead2 = (Map<Long, String>) input.readObject();
        Map<Long, String> mapRead3 = (Map<Long, String>) input.readObject();
        input.close();
        cfr.close();

        assertSameMap(map, mapRead);
        assertSameMap(map, mapRead2);
        assertSameMap(map, mapRead3);
    }

    /**
     * Verifies that if you call addFile and then call addFile again
     * you will get an IllegalStateException exception.
     * @throws IOException problem reading/writing
     * @throws ClassNotFoundException problem deserializing
     */
    @Test(expected = IllegalStateException.class)
    public void testMultiAddFileWrong() throws IOException, ClassNotFoundException {
        new File("test-data/CompoundFile4.dat").delete();
        CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile4.dat");
        CompoundDataOutput output = cfw.addFile("file1");
        output.writeObject("hello");
        // Shouldn't be able to do this -  need to output.close() first
        // None of the code past here will be executed
        output = cfw.addFile("file2");
        output.writeObject("hello again");
        output.close();
        cfw.close();
    }

    /**
     * This writes a bunch of small files and verifies the
     * data comes back from the files. Here we write
     * and read the data in a multi-threaded fashion.
     * @throws IOException problem reading/writing
     */
    @Test
    public void testMultiThreaded() throws Exception {
        final int numThreads = 50;
        final int numFiles = 20000;

        new File("test-data/CompoundFile5.dat").delete();

        final CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile5.dat");
        new ParallelTeam(numThreads).execute( new ParallelRegion() {
            public void run() throws Exception {
                execute(0, numFiles - 1, new IntegerForLoop() {
                    public void run (int first, int last) throws IOException {
                        for (int r = first; r <= last; r++) {
                            final CompoundDataOutput output = cfw.addFile("file" + r);
                            output.writeUTF("Data for file " + r);
                            output.close();
                        }
                    }
                });
            }
        });
        cfw.close();

        final CompoundFileReader cfr = new CompoundFileReader("test-data/CompoundFile5.dat", numThreads);
        long startTime = System.currentTimeMillis();
        new ParallelTeam(numThreads).execute( new ParallelRegion() {
            public void run() throws Exception {
                execute(0, numFiles - 1, new IntegerForLoop() {
                    public void run (int first, int last) throws IOException, ClassNotFoundException {
                        for (int r = first; r <= last; r++) {
                            final CompoundDataInput input = cfr.readFile("file" + r);
                            assertEquals("Data for file " + r, input.readUTF());
                            input.close();
                        }
                    }
                });
            }
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Time to " + numThreads + "-threaded read " + numFiles
                + " files " + (endTime - startTime));
        cfr.close();
    }

    /**
     * Method to compare two Map[Long, String]'s to verify
     * the contain the same contents.
     * @throws IOException problem reading/writing
     */
    public void assertSameMap(final Map<Long, String> expected, final Map<Long, String> actual) {
        assertEquals("Map sizes differ.", expected.size(), actual.size());
        for (Long key : expected.keySet()) {
            assertTrue("Expected to find key " + key, actual.containsKey(key));
            String value = expected.get(key);
            assertTrue("Expected to find value " + value, actual.containsValue(value));
        }
    }
}
