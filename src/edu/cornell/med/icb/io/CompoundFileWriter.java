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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.DataOutput;
import java.io.Closeable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Write a compound file. Only one thread should be writing to the compound
 * file at a time. NOT THREAD SAFE!
 * TODO: * Add a semaphore and make it thread safe? This will probably require
 * TODO: that finishAddFile be a required operation, not do it automatically.
 * TODO: * Locally track the file contents so we don't need to rely on the
 * TODO: CompoundFileReader and the bulk mode.
 * @author Kevin Dorff
 */
public class CompoundFileWriter implements Closeable {

    /**
     * Used to log debug and informational messages.
     */
    private static final Log LOG = LogFactory.getLog(CompoundFileWriter.class);

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
     * (i.e., NOT deleted).
     */
    public static final int FILE_STATE_NORMAL = 0;

    /**
     * This state denotes that a file in the compound file is deleted.
     */
    public static final int FILE_STATE_DELETED = 1;

    /**
     * A file reader, to scan the file at startup, etc.
     */
    private final CompoundFileReader compoundFileReader;

    /**
     * The filename of the compound file.
     */
    private final String filename;

    /**
     * If we are in bulk load mode.
     */
    private boolean bulkLoadMode;

    /**
     * Create (if it doesn't exist) or append to (if it does exist)
     * a compound file.
     * @param filename the compound file to write to
     * @throws IOException problem opening the file
     */
    public CompoundFileWriter(final String filename) throws IOException {
        super();

        this.filename = filename;
        stream = new RandomAccessFile(new File(filename), "rw");
        compoundFileReader = new CompoundFileReader(filename);
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
     * @throws java.io.IOException error reading the directory
     */
    public void setBulkLoadMode(final boolean bulkLoadMode) throws IOException {
        if (!bulkLoadMode) {
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
     * @return a DataOutput which can be used to write the contents
     * of the file
     */
    public DataOutput addFile(final String name) throws IOException {
        if (stream == null) {
            throw new IllegalStateException("CompoundFileWriter is not open.");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name specified was null or empty.");
        }

        finishAddFile();

        if (containsFile(name)) {
            throw new IOException("The compound file " + filename
                    + " already contains a file named " + name);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Adding a new file named " + name);
        }
        totalNumberOfFiles++;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Seeking to 0 to write new totalNumberOfFiles " + totalNumberOfFiles);
        }
        stream.seek(0);
        stream.writeLong(totalNumberOfFiles);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Seeking to " + stream.length());
        }
        stream.seek(stream.length());
        stream.writeInt(FILE_STATE_NORMAL);
        stream.writeUTF(name);
        sizePositionAtAddStart = stream.length();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Remebering size position " + sizePositionAtAddStart);
        }
        stream.writeLong(0);  // Length will go here
        if (LOG.isTraceEnabled()) {
            LOG.trace("Data starting at " + stream.length());
        }
        lengthAtAddStart = stream.length();
        return stream;
    }

    /**
     * Write an object to the current stream position.
     * @param objToWrite the object to write
     * @throws IOException error reading the object
     */
    public void writeObject(final Object objToWrite) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(objToWrite);
        out.close();
        // Get the bytes of the serialized object
        byte[] buf = bos.toByteArray();
        // save the position and length of the serialized object
        stream.writeInt(buf.length);
        stream.write(buf);
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
    public boolean containsFile(final String name) {
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
        if (containsFile(name)) {
            finishAddFile(false);
            final long position = compoundFileReader.nameToFileStartPositionMap.get(name);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Marking file deleted at position " + position);
            }
            stream.seek(position);
            stream.writeInt(FILE_STATE_DELETED);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Rescanning directory");
            }
            compoundFileReader.scanDirectory();
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Not deleting, not in compound file");
            }
        }
    }

    /**
     * Close the CompoundFileWriter.
     * @throws IOException problem closing, likely the problem is with
     * finishAddFile(), if so the CompoundFile is probably un-usable.
     */
    public void close() throws IOException {
        if (stream != null) {
            finishAddFile();
            stream.close();
            stream = null;
        }
    }

    /**
     * Finish adding a file (calling addFile again or close will do this
     * automatically). Setting bulkLoadMode to true will make this faster.
     * @throws IOException error scanning the directory
     */
    public void finishAddFile() throws IOException {
        finishAddFile(!bulkLoadMode);
    }

    /**
     * Finish adding a file to the compound file.
     * @param updateDirectory if true, the directory will be rescanned after the add is finished
     * @throws IOException problem finishing addFile. The CompoundFile
     * is probably un-usable.
     */
    private void finishAddFile(final boolean updateDirectory) throws IOException {
        if (lengthAtAddStart == -1) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("skipping finish add...");
            }
            return;
        }
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("running finish add...");
            }
            final long dataSize = stream.length() - lengthAtAddStart;
            if (dataSize > 0) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("++ data size was " + dataSize + " writing at position " + sizePositionAtAddStart);
                }
                stream.seek(sizePositionAtAddStart);
                stream.writeLong(dataSize);
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("++ ZERO data size.");
                }
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
