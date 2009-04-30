/*
 * Copyright (C) 2008-2009 Institute for Computational Biomedicine,
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * This comes from http://snippets.dzone.com/posts/show/3523
 * The author, David R. MacIver, has released it under WTFPL.
 * http://snippets.dzone.com/posts/show/3766
 * http://sam.zoy.org/wtfpl/
 *
 * ------------------------------------------------------------------
 * An iterator that 'flattens out' collections, iterators, arrays, etc.
 * <p/>
 * That is it will iterate out their contents in order, descending into any
 * iterators, iterables or arrays provided to it.
 * <p/>
 * An example (not valid Java for brevity - some type declarations are ommitted):
 * <p/>
 * new FlattingIterator({1, 2, 3}, {{1, 2}, {3}}, new ArrayList({1, 2, 3}))
 * <p/>
 * Will iterate through the sequence 1, 2, 3, 1, 2, 3, 1, 2, 3.
 * <p/>
 * Note that this implements a non-generic version of the Iterator interface so
 * may be cast appropriately - it's very hard to give this class an appropriate
 * generic type.
 *
 * @author David R. MacIver
 */
public class FlatteningIterator implements Iterator {
    /**
     * Marker object. This is never exposed outside this class, so can be guaranteed
     * to be != anything else. We use it to indicate an absense of any other object.
     */
    private final Object blank = new Object();

    /**
     *  This stack stores all the iterators found so far. The head of the stack is
     * the iterator which we are currently progressing through.
     */
    private final Stack<Iterator< ? >> iterators = new Stack<Iterator< ? >>();

    /**
     * Storage field for the next element to be returned. blank when the next element
     * is currently unknown.
     */
    private Object next = blank;

    /**
     * Create a flattening iterator with the given objects.
     * @param objects to create an iterator from.
     */
    public FlatteningIterator(final Object... objects) {
        this.iterators.push(Arrays.asList(objects).iterator());
    }

    /**
     * Remove, does nothing.
     */
    public void remove() {
        /* Not implemented */
    }

    /**
     * Move to the next.
     */
    private void moveToNext() {
        if ((next == blank) && !this.iterators.empty()) {
            if (!iterators.peek().hasNext()) {
                iterators.pop();
                moveToNext();
            } else {
                final Object nextOne = iterators.peek().next();
                if (nextOne instanceof Iterator) {
                    iterators.push((Iterator< ? >) nextOne);
                    moveToNext();
                } else if (nextOne instanceof Iterable) {
                    iterators.push(((Iterable) nextOne).iterator());
                    moveToNext();
                } else if (nextOne instanceof Array) {
                    iterators.push(Arrays.asList((Array) nextOne).iterator());
                    moveToNext();
                } else {
                    this.next = nextOne;
                }
            }
        }
    }

    /**
     * Returns the next element in our iteration, throwing a NoSuchElementException
     * if none is found.
     * @return the next element in the iteration
     */
    public Object next() {
        moveToNext();

        if (this.next == blank) {
            throw new NoSuchElementException();
        } else {
            final Object nextOne = this.next;
            this.next = blank;
            return nextOne;
        }
    }

    /**
     * Returns if there are any objects left to iterate over. This method
     * can change the internal state of the object when it is called, but repeated
     * calls to it will not have any additional side effects.
     * @return true if there is a next object
     */
    public boolean hasNext() {
        moveToNext();
        return (this.next != blank);
    }
}
