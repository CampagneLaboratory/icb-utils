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

package edu.cornell.med.icb.iterators;

import java.util.Iterator;

/**
 * Class that takes an {@link java.util.Iterator} and makes it {@link Iterable}
 * so it can be used with a java foreach loop. Based on the code by Jesse Wilson at
 * <a href="http://weblogs.java.net/blog/forax/archive/2006/09/use_for_to_iter.html">
 * http://weblogs.java.net/blog/forax/archive/2006/09/use_for_to_iter.html</a>
 * @author Kevin Dorff, Jesse Wilson
 */
public final class IteratorIterable<T> implements Iterable<T> {
    /** The iterator we are augmenting. */
    private final Iterator<T> iterator;

    /**
     * Constructor, to convert the iterator.
     * @param iteratorSrc the iterator to augment
     */
    public IteratorIterable(final Iterator<T> iteratorSrc) {
        super();
        this.iterator = iteratorSrc;
    }

    /**
     * The augmented iterator.
     * @return The augmented iterator
     */
    public Iterator<T> iterator() {
        return iterator;
    }
}
