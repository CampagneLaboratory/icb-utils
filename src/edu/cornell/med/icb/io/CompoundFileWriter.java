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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.DataOutput;

/**
 * Write a compound file. Only one thread should be writing to the compound
 * file at a time. NOT THREAD SAFE!
 * @author Kevin Dorff
 */
public class CompoundFileWriter {

    /**
     * The stream we are writing to.
     */
    private RandomAccessFile stream;

    /**
     * The position of the current data-size element in the current "addFile"
     * or -1 if we aren't currently in an "addFile".
     */
    private long sizePositionAtAddStart;

    /**
     * The total length of the file at the start of "addFile". This is
     * used to determine the size of the data in finishAddFile.
     */
    private long lengthAtAddStart;

    /**
     * The current total number of files in this compound file.
     */
    private long totalNumberOfFiles;

    /**
     * This state denotes that a file in the compound file is normal
     * (ie NOT deleted).
     */
    public final static int FILE_STATE_NORMAL = 0;

    /**
     * This state denotes that a file in the compound file is deleted.
     */
    public final static int FILE_STATE_DELETED = 1;

    /**
     * A file reader, to scan the file at startup, etc.
     */
    final private CompoundFileReader compoundFileReader;

    /** The filename of the compound file. */
    final private String filename;

    /** If we are in bulk load mode. */
    private boolean bulkLoadMode;

    /**
     * Create (if it doesn't exist) or append to (if it does exist)
     * a compound file.
     * @param filename the compound file to write to
     * @throws IOException problem opening the file
     */
    public CompoundFileWriter(final String filename) throws IOException {
        compoundFileReader = new CompoundFileReader(filename);

        this.filename = filename;
        stream = new RandomAccessFile(new File(filename), "rw");
        sizePositionAtAddStart = -1;
        lengthAtAddStart = -1;
        stream.seek(0);
        if (stream.length() == 0) {
            stream.writeLong(0);
            totalNumberOfFiles = 0;
        } else {
            totalNumberOfFiles = stream.readLong();
        }
        bulkLoadMode = true;
    }

    /**
     * Get the compoundFileReader associated with this compoundFileWriter.
     * If in bulkLoadMode the reader will not stay up to date.
     * Either turn off bulkLoadMode or call reader.scanDirectory()
     * to update.
     * @return the compoundFileReader associated with this compoundFileWriter.
     */
    public CompoundFileReader getCompoundFileReader() {
        return compoundFileReader;
    }

    /**
     * Get if in bulk load mode.
     * @return if in bulk load mode.
     */
    public boolean getBulkLoadMode() {
        return bulkLoadMode;
    }

    /**
     * Set if in bulk load mode.
     * @param bulkLoadMode if in bulk load mode.
     */
    public void setBulkLoadMode(final boolean bulkLoadMode) throws IOException {
        if (bulkLoadMode == false) {
            compoundFileReader.scanDirectory();
        }
        this.bulkLoadMode = bulkLoadMode;
    }

    /**
     * Add a file to the compound file. This needs to be "completed"
     * by calling addFile(...) again, calling close(), or
     * by calling finishAddFile().
     * @param name the internal filename (any string is valid)
     * @throws IOException problem adding a file
     */
    public DataOutput addFile(final String name) throws IOException {
        if (stream == null) {
            throw new IOException("CompoundFileWriter is not open.");
        }
        if (name == null || name.length() == 0) {
            throw new IOException("The name specified to addFile was null or empty.");
        }
        finishAddFile();

        if (containsFile(name)) {
            throw new IOException("The compound file " + filename
                    + " already contains a file named " + name);
        }

        // System.out.println("Adding a new file named " + name);
        totalNumberOfFiles++;
        // System.out.println("Seeking to 0 to write new totalNumberOfFiles " + totalNumberOfFiles);
        stream.seek(0);
        stream.writeLong(totalNumberOfFiles);

        // System.out.println("Seeking to " + stream.length());
        stream.seek(stream.length());
        stream.writeInt(FILE_STATE_NORMAL);
        stream.writeUTF(name);
        sizePositionAtAddStart = stream.length();
        // System.out.println("Remebering size position " + sizePositionAtAddStart);
        stream.writeLong(0);  // Length will go here
        // System.out.println("Data starting at " + stream.length());
        lengthAtAddStart = stream.length();
        return stream;
    }

    /**
     * Returns true of a file with the specified name exists in this
     * compound file. This will potentially not give back the correct
     * information if in bulkLoadMode. To be completely up to date
     * if you are in bulkLoadMode you just call
     * compoundFileReader.scanDirectory() first.
     * @param name the name of the file to check for
     * @return true of the file exists in the compound file
     */
    public boolean containsFile(final String name) throws IOException {
        return compoundFileReader.nameToDataPositionMap.containsKey(name);
    }

    /**
     * Delete a file with the given name. Note: this doesn't free up
     * the space taken in the compound file, just the file won't be read
     * again. To write a file with the same name, the previous version must
     * be deleted.
     * @param name the name of the file to delete.
     * @throws IOException problem deleting the file
     */
    public void deleteFile(final String name) throws IOException {
        if (!containsFile(name)) {
            // System.out.println("Not deleting, not in compound file");
            return;
        }
        finishAddFile(false);
        long position = compoundFileReader.nameToFileStartPositionMap.get(name);
        // System.out.println("Marking file deleted at position " + position);
        stream.seek(position);
        stream.writeInt(FILE_STATE_DELETED);
        // System.out.println("Rescanning directory");
        compoundFileReader.scanDirectory();
    }

    /**
     * Close the CompoundFileWriter.
     * @throws IOException problem closing, likely the problem is with
     * finishAddFile(), if so the CompoundFile is probably un-usable.
     */
    public void close() throws IOException {
        finishAddFile();
        stream.close();
        stream = null;
    }

    /**
     * Finish adding a file (calling addFile again or close will do this
     * automatically). Setting bulkLoadMode to true will make this faster.
     * @throws IOException
     */
    public void finishAddFile() throws IOException {
        finishAddFile(!bulkLoadMode);
    }

    /**
     * Finish adding a file to the compound file.
     * @throws IOException problem finishing addFile. The CompoundFile
     * is probably un-usable.
     */
    private void finishAddFile(final boolean updateDirectory) throws IOException {
        if (lengthAtAddStart == -1) {
            // System.out.println("skipping finish add...");
            return;
        }
        try {
            // System.out.println("running finish add...");
            long dataSize = stream.length() - lengthAtAddStart;
            if (dataSize > 0) {
                // System.out.println("++ data size was " + dataSize + " writing at position " + sizePositionAtAddStart);
                stream.seek(sizePositionAtAddStart);
                stream.writeLong(dataSize);
            } else {
                // System.out.println("++ ZERO data size.");
            }
        } finally {
            sizePositionAtAddStart = -1;
            lengthAtAddStart = -1;
            if (updateDirectory) {
                compoundFileReader.scanDirectory();
            }
        }
    }
}
