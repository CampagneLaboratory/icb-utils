/*
 * Copyright (C) 2007-2008 Institute for Computational Biomedicine,
 *               Weill Medical College of Cornell University
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

package edu.cornell.med.icb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class extends the PriorityQueue to implement a Bounded version,
 * a version that will contain no more than maxCapacity elements.
 * Any elements added (via add(), addAll(), or offer())
 * will still be added to the Queue (so proper order is maintained) but
 * then if the maxCapacity is exceeded, any extra elements (at the
 * head or tail,  depending on the value of removeFromFront) will be
 * removed, which will be faster if removeFromFront == true.
 * If removeFromFront == false the queue will need to be rebuilt after
 * every add(), addAll(), or offer() operation because of the implemetation
 * of the PriorityQueue.iterator() method and the lack of random access
 * to the queue.
 * @author Kevin Dorff (Oct 19, 2007)
 */
public class BoundedPriorityQueue<E> extends PriorityQueue<E> {

    private int maxCapacity;
    private boolean removeFromFront;

    /**
     * Create a BoundedPriorityQueue specifying the max capacity,
     * the comparator to use to order the queue, and if surplus
     * elements should be removed form the head or tail.
     * @param maxCapacity the maximum capacity of the queue
     * @param c the comparator to use to order the queue
     * @param removeFromFront if true, surplus elements will
     * be removed from the front of the queue.
     */
    public BoundedPriorityQueue(final int maxCapacity, final Comparator<E> c,
                        final boolean removeFromFront) {
        super(maxCapacity + 1, c);
        this.maxCapacity = maxCapacity;
        this.removeFromFront = removeFromFront;
    }

    /**
     * Create a BoundedPriorityQueue specifying the max capacity,
     * the comparator to use to order the queue. Any surplus
     * will be removed from the tail of the queue.
     * @param maxCapacity the maximum capacity of the queue
     * @param c the comparator to use to order the queue
     */
    public BoundedPriorityQueue(final int maxCapacity, final Comparator<E> c) {
        this(maxCapacity, c, false);
    }

    /**
     * Create a BoundedPriorityQueue specifying the max capacity.
     * Since no comparator is provided, the queue will be built
     * in natural order. You can also specify if surplus elements
     * should be removed form the head or tail.
     * @param maxCapacity the maximum capacity of the queue
     * @param removeFromFront if true, surplus elements will
     * be removed from the front of the queue.
     */
    public BoundedPriorityQueue(final int maxCapacity, final boolean removeFromFront) {
        super(maxCapacity);
        this.maxCapacity = maxCapacity;
        this.removeFromFront = removeFromFront;
    }

    /**
     * Create a BoundedPriorityQueue specifying the max capacity.
     * Since no comparator is provided, the queue will be built
     * in natural order. Surplus elements will be removed from
     * the tail of the queue.
     * @param maxCapacity the maximum capacity of the queue
     */
    public BoundedPriorityQueue(final int maxCapacity) {
        this(maxCapacity, false);
    }

    /**
     * Get the max capacity of the queue.
     * @return max capacity of the queue
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Set the max capacity of the queue.
     * @param maxCapacity max capacity of the queue
     */
    public void setMaxCapacity(final int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Get if surplus elements should be removed from the
     * front of the queue.
     * @return true if surplus elements should be removed
     * from the front of the queue.
     */
    public boolean isRemoveFromFrontSize() {
        return removeFromFront;
    }

    /**
     * Set if surplus elements should be removed from the
     * front of the queue.
     * @param removeFromFront true if surplus elements should
     * be removed from the front of the queue.
     */
    public void setRemoveFromFrontSize(final boolean removeFromFront) {
        this.removeFromFront = removeFromFront;
    }

    /**
     * Add a element to the queue then ensure then queue isn't
     * larger than maxCapacity
     * @param e the element to add to the queue
     * @return true. The return value doesn't say anything about
     * if the element (or any element)  was clipped when the
     * maxCapacity of the queue was ensured.
     */
    public boolean add(E e) {
        boolean result = super.add(e);
        ensureSize();
        return result;
    }

    /**
     * Add a collection of elements to the queue then ensure
     * the queue isn't larger than maxCapacity
     * @param c the collection of elements to add to the queue
     * @return true. The return value doesn't say anything about
     * if the element (or any element)  was clipped when the
     * maxCapacity of the queue was ensured.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = super.addAll(c);
        ensureSize();
        return result;
    }

    /**
     * Offer (add) an element to the queue then ensure then queue isn't
     * larger than maxCapacity
     * @param e the element to add to the queue
     * @return true. The return value doesn't say anything about
     * if the element (or any element)  was clipped when the
     * maxCapacity of the queue was ensured.
     */
    @Override
    public boolean offer(E e) {
        boolean result = super.offer(e);
        ensureSize();
        return result;
    }

    /**
     * Reverse the queue to a list. This can be done
     * either destructively or non-distructively.
     * If destructive is false, this can be somewhat expensive
     * as the queue will need to be rebuilt as we don't have
     * random access to the queue elements.
     * @param destructive if the queue should be consumed
     * as the reverse list is created.
     * @return the values of the queue in a List but in
     * reverse order
     */
    public synchronized List<E> reversedList(boolean destructive) {
        int queueSize = this.size();
        LinkedList<E> reverse = new LinkedList<E>();
        for (int i = 0; i < queueSize; i++) {
            E current = this.poll();
            reverse.addFirst(current);
        }
        if (!destructive) {
            this.addAll(reverse);
        }
        return reverse;
    }

    /**
     * Ensures the size of the queue is <= maxSize. This will be faster if
     * removeFromFront == true, otherwise the queue will have to be rebuilt
     * due to limitations on iterator(), notably from PriorityQueue.
     * If  removeFromFront == false the queue will need to be rebuilt
     * every time this is called (once per LimitedQueue creation and once
     * per add(), addAll(), offer(). This is run synchronized
     */
    private synchronized void ensureSize() {
        int size = this.size();
        if (size <= maxCapacity) {
            return;
        }
        if (removeFromFront) {
            for (int i = 0; i < size - maxCapacity; i++) {
                this.poll();
            }
        } else {
            // Remove from the END of the queue, which is the
            // same as keep maxSize elements from the front of
            // the queue.
            List<E> transferQueue = new ArrayList<E>(maxCapacity);
            for (int i = 0; i < maxCapacity; i++) {
                transferQueue.add(this.poll());
            }
            this.clear();
            // Here we want super, we don't want to
            // call ensureSize() again
            super.addAll(transferQueue);
        }
    }
}
