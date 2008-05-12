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
import java.io.DataInput;
import java.io.FileNotFoundException;
import java.io.Closeable;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Read a compound file.
 * TODO: Improve the data storage, change from the maps to a map of objects.
 * TODO: May want to investigate MultipleStream from fastutil - this would allow iterating files
 * @author Kevin Dorff
 */
public class CompoundFileReader implements Closeable {
    /**
     * Used to log debug and informational messages.
     */
    private static final Log LOG = LogFactory.getLog(CompoundFileReader.class);

    /**
     * The filename of the compound file.
     */
    private final String filename;

    /**
     * The stream we are reading from.
     */
    private RandomAccessFile stream;

    /** Name of file in the container to the position the file starts at. */
    Map<String, Long> nameToFileStartPositionMap;

    /** Name of file in the container to the position the file's data starts at. */
    Map<String, Long> nameToDataPositionMap;

    /** Name of file in the container to the size of the file. */
    Map<String, Long> nameToSizeMap;

    /** The total number of files stored in the compound file, included deleted files. */
    private long totalNumberOfFiles;

    /**
     * Create (if it doesn't exist) or open to (if it does exist)
     * a compound file.
     * @param filename the compound file to read from
     * @throws IOException problem opening the file
     */
    public CompoundFileReader(final String filename) throws IOException {
        super();
        this.filename = filename;
        stream = new RandomAccessFile(new File(filename), "r");
        scanDirectory();
    }

    /**
     * Get the total number of files stored in the compound file, included deleted files.
     * @return the total number of files stored in the compound file
     */
    public long getTotalNumberOfFiles() {
        return totalNumberOfFiles;
    }

    /**
     * Read a file from the compound file.
     * @param name the name of the compound file to read
     * @return a DataInput object to read the actual data
     * @throws IOException problem reading the file
     */
    public DataInput readFile(final String name) throws IOException {
        if (stream == null) {
            throw new IllegalStateException("CompoundFileReader is not open.");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name specified was null or empty.");
        }

        final Long position = nameToDataPositionMap.get(name);
        if (position == null) {
            throw new FileNotFoundException("The compound file " + filename
                    + " does not contain the file " + name);
        }
        stream.seek(position);
        return stream;
    }

    /**
     * Read an object from the current stream position.
     * @return the object
     * @throws IOException error reading the object
     * @throws ClassNotFoundException error de-serializing the object
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        int size = stream.readInt();
        byte[] buf = new byte[size];
        stream.readFully(buf);
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object deserializedObject = ois.readObject();
        ois.close();
        return deserializedObject;
    }

    /**
     * Get the set of non-deleted filenames in the compound file.
     * @return the set of non-deleted filenames in the compound file
     */
    public Set<String> getFileNames() {
        return nameToDataPositionMap.keySet();
    }

    /**
     * Get the file size for a specific file in the compound file.
     * @param name the name of the file to get the size for
     * @return the size of the named file
     * @throws FileNotFoundException if the file named does not exist in the compound file
     */
    public long getFileSize(final String name) throws FileNotFoundException {
        final Long size = nameToSizeMap.get(name);
        if (size == null) {
            throw new FileNotFoundException("The compound file " + filename
                    + " does not contain the file " + name);
        }
        return size;
    }

    /**
     * Force a re-scan of the contents of the compound file.
     * @throws IOException problem reading the compound file
     */
    public void scanDirectory() throws IOException {
        nameToDataPositionMap = new LinkedHashMap<String, Long>();
        nameToFileStartPositionMap = new LinkedHashMap<String, Long>();
        nameToSizeMap = new LinkedHashMap<String, Long>();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Scanning directory from " + filename);
        }
        if (stream.length() != 0) {
            stream.seek(0);
            totalNumberOfFiles = stream.readLong();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Total number of files " + totalNumberOfFiles);
            }
            for (int i = 0; i < totalNumberOfFiles; i++) {
                final long fileStartPosition = stream.getFilePointer();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Reading file starting at position " + fileStartPosition);
                }
                final int fileState = stream.readInt();
                final String fileName = stream.readUTF();
                final long fileSize = stream.readLong();
                final long dataPosition = stream.getFilePointer();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("File " + fileName + " has a state " + fileState
                            + " size " + fileSize);
                }
                if (fileState == CompoundFileWriter.FILE_STATE_NORMAL) {
                    nameToFileStartPositionMap.put(fileName, fileStartPosition);
                    nameToDataPositionMap.put(fileName, dataPosition);
                    nameToSizeMap.put(fileName, fileSize);
                }
                stream.seek(dataPosition + fileSize);
            }
        }
    }

    /**
     * Returns true of a file with the specified name exists in this
     * compound file. To be completely up to date call
     * compoundFileReader.scanDirectory() first.
     * @param name the name of the file to check for
     * @return true of the file exists in the compound file
     */
    public boolean containsFile(final String name) {
        return nameToDataPositionMap.containsKey(name);
    }

    /**
     * Close the compound file.
     * @throws IOException error closing the compound file
     */
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }
}