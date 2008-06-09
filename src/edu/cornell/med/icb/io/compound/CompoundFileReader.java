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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.Closeable;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Read a compound file.
 * This class uses a stream pool to control the number of threads that can
 * simulatneously read from the file at once, you can use the constructor to
 * tune this pool size to the number of threads that will need to access it
 * simultaneously. When you readFile() you will receive a CompoundDataInput
 * for reading from the file - you MUST call close() on that object when you
 * are done reading or the pool will quickly empty and your program will hang.
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

    /** Name of file in the container to directory entry data. */
    private Map<String, CompoundDirectoryEntry> nameToDirEntryMap;

    /** The total number of files stored in the compound file, included deleted files. */
    private long totalNumberOfFiles;

    /** The pool of streams for reading files from this compound file. */
    private final ArrayBlockingQueue<RandomAccessFile> streamPool;

    /**
     * Create (if it doesn't exist) or open to (if it does exist)
     * a compound file. This uses a stream pool of size 5 (the
     * number of files that can be read at once in a multi-threaded
     * application).
     * @param physicalFilename the compound file to read from
     * be read at once in a multi-threaded application).
     * @throws IOException problem opening the file
     */
    public CompoundFileReader(final String physicalFilename) throws IOException {
        this(physicalFilename, 5);
    }

    /**
     * Create (if it doesn't exist) or open to (if it does exist)
     * a compound file.
     * @param physicalFilename the compound file to read from
     * @param streamsPoolSize the size of the streams pool (number of files that can
     * be read at once in a multi-threaded application).
     * @throws IOException problem opening the file
     */
    public CompoundFileReader(final String physicalFilename, final int streamsPoolSize)
            throws IOException {
        super();
        this.filename = physicalFilename;
        streamPool = new ArrayBlockingQueue<RandomAccessFile>(streamsPoolSize);
        for (int i = 0; i < streamsPoolSize; i++) {
            streamPool.add(new RandomAccessFile(physicalFilename, "r"));
        }
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
     * Read a file from the compound file. You >> MUST CALL close() << on the
     * CompoundDataInput when you are done reading the file. If you don't
     * call close() you will run out of readers and your program will hang.
     * @param name the name of the compound file to read
     * @return a DataInput object to read the actual data
     * @throws IOException problem reading the file
     */
    public CompoundDataInput readFile(final String name) throws IOException {
        final RandomAccessFile stream = borrowFromPool();

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name specified was null or empty.");
        }


        final CompoundDirectoryEntry entry = nameToDirEntryMap.get(name);
        if (entry == null) {
            throw new FileNotFoundException("The compound file " + filename
                    + " does not contain the file " + name);
        }
        final long position = entry.getDataPosition();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading an file that should be " + entry.getFileSize() + " bytes long");
        }
        stream.seek(position);
        return new CompoundDataInput(stream, this);
    }

    /**
     * Borrow a RandomAccessFile from the pool.
     * @return the RandomAccessFile from the pool
     * @throws IOException thread interrupted waiting to get the object from the pool
     */
    RandomAccessFile borrowFromPool() throws IOException {
        try {
            return streamPool.take();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    /**
     * Return a RandomAccessFile to the pool.
     * @param stream the RandomAccessFile to return to the pool
     */
    void returnToPool(final RandomAccessFile stream) {
        try {
            streamPool.put(stream);
        } catch (InterruptedException e) {
            LOG.error("Error returning stream to pool ", e);
        }
    }

    /**
     * Get the set of non-deleted filenames in the compound file.
     * @return the set of non-deleted filenames in the compound file
     */
    public Set<String> getFileNames() {
        return nameToDirEntryMap.keySet();
    }

    /**
     * Get the file size for a specific file in the compound file.
     * @param name the name of the file to get the size for
     * @return the size of the named file
     * @throws FileNotFoundException if the file named does not exist in the compound file
     */
    public long getFileSize(final String name) throws FileNotFoundException {
        final CompoundDirectoryEntry entry = nameToDirEntryMap.get(name);
        if (entry == null) {
            throw new FileNotFoundException("The compound file " + filename
                    + " does not contain the file " + name);
        } else {
            return entry.getFileSize();
        }
    }

    /**
     * Force a re-scan of the contents of the compound file.
     * @throws IOException problem reading the compound file
     */
    public synchronized void scanDirectory() throws IOException {
        nameToDirEntryMap = new LinkedHashMap<String, CompoundDirectoryEntry>();

        final RandomAccessFile stream = borrowFromPool();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning directory from " + filename);
        }
        if (stream.length() != 0) {
            stream.seek(0);
            totalNumberOfFiles = stream.readLong();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Total number of files " + totalNumberOfFiles);
            }
            for (int i = 0; i < totalNumberOfFiles; i++) {
                final long fileStartPosition = stream.getFilePointer();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reading file starting at position " + fileStartPosition);
                }
                final int fileState = stream.readInt();
                final String fileName = stream.readUTF();
                final long fileSize = stream.readLong();
                final long dataPosition = stream.getFilePointer();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File " + fileName + " has a state " + fileState
                            + " size " + fileSize);
                }
                if (fileState == CompoundFileWriter.FILE_STATE_NORMAL) {
                    final CompoundDirectoryEntry dirEntry = new CompoundDirectoryEntry(
                                    fileName, fileStartPosition, dataPosition, fileSize);
                    nameToDirEntryMap.put(fileName, dirEntry);
                }
                stream.seek(dataPosition + fileSize);
            }
        }
        returnToPool(stream);
    }

    /**
     * Returns true of a file with the specified name exists in this
     * compound file. To be completely up to date call
     * compoundFileReader.scanDirectory() first.
     * @param name the name of the file to check for
     * @return true of the file exists in the compound file
     */
    public boolean containsFile(final String name) {
        return nameToDirEntryMap.containsKey(name);
    }

    /**
     * Close the compound file.
     * @throws IOException error closing the compound file
     */
    public void close() throws IOException {
    }

    /**
     * Add a directory entry.
     * NOTE this does NOT
     * change the underlying compound file, just the in-memory
     * directory entries map that was created when
     * scanDirectory() was called. This allows CompoundFileWriter to update
     * the directory when addFile() is called.
     * @param entry the entry to add
     */
    void addToDirectory(final CompoundDirectoryEntry entry) {
        nameToDirEntryMap.put(entry.getName(), entry);
    }

    /**
     * Remove a directory entry.
     * NOTE this does NOT
     * change the underlying compound file, just the in-memory
     * directory entries map that was created when
     * scanDirectory() was called. This allows CompoundFileWriter to update
     * the directory when deleteFile() is called.
     * @param entryName the file name of the entry to remove
     * from the directory.
     */
    void removeFromDirectory(final String entryName) {
        nameToDirEntryMap.remove(entryName);
    }

    /**
     * Get the directory entry for a specific file or null
     * of a file with that name is not in the directory.
     * @param name filename to get the directory entry for
     * @return the direcotry entry for the named file
     */
    public CompoundDirectoryEntry getDirectoryEntry(final String name) {
        return nameToDirEntryMap.get(name);
    }

    /**
     * Get the complete directory.
     * @return the complete directory.
     */
    public Collection<CompoundDirectoryEntry> getDirectory() {
        return nameToDirEntryMap.values();
    }
}
