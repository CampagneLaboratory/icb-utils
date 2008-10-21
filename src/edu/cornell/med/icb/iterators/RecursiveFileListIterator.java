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

package edu.cornell.med.icb.iterators;

import edu.cornell.med.icb.iterators.FlatteningIterator;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This comes from http://snippets.dzone.com/posts/show/3532
 * The author, David R. MacIver, has released it under WTFPL.
 * http://snippets.dzone.com/posts/show/3766
 * http://sam.zoy.org/wtfpl/
 *
 * ------------------------------------------------------------------
 * Iterates over all non-directory files contained in some subdirectory of the
 * current one.
 *
 * @author David R. MacIver
 */
public class RecursiveFileListIterator implements Iterator<File> {
    /** The flattening iterator to use. */
    private final FlatteningIterator flatteningIterator;

    /**
     * Remove, does nothing.
     */
    public void remove() {
    }

    /**
     * Create a new RecursiveFileListIterator.
     * @param file the starting directory
     * @param filter the file filter
     */
    public RecursiveFileListIterator(final File file, final FileFilter filter) {
        this.flatteningIterator = new FlatteningIterator(new FileIterator(file, filter));
    }

    /**
     * Create a new RecursiveFileListIterator.
     * @param file the starting directory
     */
    public RecursiveFileListIterator(final File file) {
        this(file, null);
    }


    /**
     * If there is a next file.
     * @return true if there is a next file
     */
    public boolean hasNext() {
        return flatteningIterator.hasNext();
    }

    /**
     * Return the next file.
     * @return the next file
     */
    public File next() {
        return (File) flatteningIterator.next();
    }

    /**
     * Iterator to iterate over all the files contained in a directory. It returns
     * a File object for non directories or a new FileIterator obejct for directories.
     */
    private static class FileIterator implements Iterator<Object> {
        /** The files iterator. */
        private final Iterator<File> files;
        /** The filter. */
        private final FileFilter filter;

        /**
         * Create a file iterator.
         * @param file the starting point
         * @param filterVal the filter
         */
        FileIterator(final File file, final FileFilter filterVal) {
            this.files = Arrays.asList(file.listFiles(filterVal)).iterator();
            this.filter = filterVal;
        }

        /**
         * Remove, does nothing.
         */
        public void remove() {
        }

        /**
         * Obtain the next object.
         * @return the next object.
         */
        public Object next() {
            File next = this.files.next();

            if (next.isDirectory()) {
                return new FileIterator(next, this.filter);
            } else {
                return next;
            }
        }

        /**
         * If there is a next obeject.
         * @return true if there is a next object
         */
        public boolean hasNext() {
            return this.files.hasNext();
        }
    }
}