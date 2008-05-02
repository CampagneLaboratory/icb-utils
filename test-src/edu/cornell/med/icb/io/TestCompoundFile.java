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
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.File;
import java.io.DataOutput;
import java.io.DataInput;
import java.util.Set;

/**
 * Describe class here.
 *
 * @author Kevin Dorff
 */
public class TestCompoundFile {

    @Test
    public void testCompoundFile() throws IOException {
        new File("test-data/CompoundFile.dat").delete();
        CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile.dat");
        DataOutput output = cfw.addFile("file1");
        output.writeUTF("File 1 string");
        output.writeLong(45);
        output.writeUTF("File 1 string B");

        output = cfw.addFile("file2");
        output.writeUTF("File 2 string");
        output.writeLong(35);
        output.writeLong(54);
        cfw.close();

        cfw = new CompoundFileWriter("test-data/CompoundFile.dat");
        output = cfw.addFile("file3");
        output.writeUTF("File 3 string");
        output.writeDouble(3.14159);
        cfw.finishAddFile();
        cfw.setBulkLoadMode(false);

        CompoundFileReader cfr = cfw.getCompoundFileReader();
        Set<String> files = cfr.getFileNames();
        for (String file : files) {
            System.out.println("Compound file contains file named " + file);
        }
        DataInput input = cfr.readFile("file1");
        assertEquals("File 1 string", input.readUTF());
        assertEquals(45, input.readLong());
        assertEquals("File 1 string B", input.readUTF());

        cfw.deleteFile("file1");
        output = cfw.addFile("file1");
        output.writeUTF("File 1b string");
        output.writeDouble(2.73);
        cfw.finishAddFile();

        input = cfr.readFile("file1");
        assertEquals("File 1b string", input.readUTF());
        assertEquals(2.73, input.readDouble(), 0.001);

        input = cfr.readFile("file2");
        assertEquals("File 2 string", input.readUTF());
        assertEquals(35, input.readLong());
        assertEquals(54, input.readLong());

        input = cfr.readFile("file3");
        assertEquals("File 3 string", input.readUTF());
        assertEquals(3.14159, input.readDouble(), 0.001);

        assertEquals(4, cfr.getTotalNumberOfFiles());
        assertEquals(3, cfr.getFileNames().size());

        cfr.close();
        cfw.close();
    }

    @Test
    public void testLotsOfSmallFiles() throws IOException {
        System.out.println("Testing lots of small files");
        new File("test-data/CompoundFile2.dat").delete();
        CompoundFileWriter cfw = new CompoundFileWriter("test-data/CompoundFile2.dat");
        for (int x = 0; x < 20000; x++) {
            DataOutput output = cfw.addFile("file" + x);
            output.writeUTF("Data for file " + x);
            if (x % 500 == 0) {
                System.out.println("Loaded " + x + " files");
            }
        }
        cfw.close();

        CompoundFileReader cfr = new CompoundFileReader("test-data/CompoundFile2.dat");
        assertEquals(20000,cfr.getFileNames().size());
    }

    @Before
    public void setup() {
    }
}
