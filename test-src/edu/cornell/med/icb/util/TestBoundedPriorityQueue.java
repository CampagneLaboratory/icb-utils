/*
 * Copyright (C) 2007-2010 Institute for Computational Biomedicine,
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

import junit.framework.TestCase;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Test the LimitedQueue class.
 * @author Kevin Dorff (Oct 22, 2007)
 */
public class TestBoundedPriorityQueue extends TestCase {

    /**
     * The list of objects to test the LimitedQueue with.
     */
    private static final List<Integer> wholeSet = new LinkedList<Integer>();
    static {
        wholeSet.add(1);
        wholeSet.add(2);
        wholeSet.add(3);
        wholeSet.add(4);
        wholeSet.add(5);
        wholeSet.add(6);
        wholeSet.add(7);
        wholeSet.add(8);
        wholeSet.add(9);
    }

    private static final List<Integer> unorderedSet = new LinkedList<Integer>();
    static {
        unorderedSet.add(3);
        unorderedSet.add(9);
        unorderedSet.add(1);
        unorderedSet.add(4);
        unorderedSet.add(8);
        unorderedSet.add(7);
        unorderedSet.add(5);
        unorderedSet.add(6);
        unorderedSet.add(2);
    }

    /**
     * This doesn't add enough to limit the Queue.
     */
    public void testLimitedQueueNoop() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4);
        queue.add(5);
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * This doesn't add enough to limit the Queue.
     */
    public void testLimitedQueueNatural() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5);
        for (final int i : wholeSet) {
            queue.add(i);
        }
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * Add each object individually. Default (remove from tail).
     */
    public void testLimitedQueueBasic() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5);
        for (final int e : wholeSet) {
            queue.add(e);
        }
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * Add each object individually. Specify remove from tail.
     */
    public void testLimitedQueueBasicFromTail() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, false);
        for (final int e : wholeSet) {
            queue.add(e);
        }
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * Add each object individually. Specify remove from front.
     */
    public void testLimitedQueueBasicFromFront() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, true);
        for (final int e : wholeSet) {
            queue.add(e);
        }
        verifyContents(queue, 5, 6, 7, 8, 9);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from tail.
     */
    public void testLimitedQueueGroupFromTail() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, false);
        queue.addAll(wholeSet);
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from front.
     */
    public void testLimitedQueueGroupFromFront() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, true);
        queue.addAll(wholeSet);
        verifyContents(queue, 5, 6, 7, 8, 9);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from tail.
     */
    public void testLimitedQueuePriQueueTailLessThan() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, new LessThanComparator(), false);
        queue.addAll(unorderedSet);
        verifyContents(queue, 1, 2, 3, 4, 5);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from tail.
     */
    public void testLimitedQueuePriQueueFrontLessThan() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, new LessThanComparator(), true);
        queue.addAll(unorderedSet);
        verifyContents(queue, 5, 6, 7, 8, 9);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from tail.
     */
    public void testLimitedQueuePriQueueTailGreaterThan() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, new GreaterThanComparator(), false);
        queue.addAll(unorderedSet);
        verifyContents(queue, 9, 8, 7, 6, 5);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from tail.
     */
    public void testLimitedQueuePriQueueFrontGreaterThan() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, new GreaterThanComparator(), true);
        queue.addAll(unorderedSet);
        verifyContents(queue, 5, 4, 3, 2, 1);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from front.
     */
    public void testQueueReverseNonDestructive() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, true);
        queue.addAll(wholeSet);
        final List<Integer> reverse = queue.reversedList(false);
        // Check the reverse
        verifyContents(reverse, 9, 8, 7, 6, 5);
        // Make sure the queue is intact with expected order
        verifyContents(queue, 5, 6, 7, 8, 9);
    }

    /**
     * Use addAll to add objects in bulk. Specify remove from front.
     */
    public void testQueueReverseDestructive() {
        final BoundedPriorityQueue<Integer> queue =
                new BoundedPriorityQueue<Integer>(5, true);
        queue.addAll(wholeSet);
        final List<Integer> reverse = queue.reversedList(true);
        // Check the reverse
        verifyContents(reverse, 9, 8, 7, 6, 5);
        // Make sure the queue is intact with expected order
        assertEquals("Queue should be emty after destructive reverse",
                0, queue.size());
    }

    /**
     * Verify the contents of the queue/list.
     * @param existing the values that are in the queue
     * @param expected that values that should be in the queue
     */
    private void verifyContents(final Collection<Integer> existing, final int... expected) {
        assertEquals("Collection size problem", expected.length, existing.size());
        int pos = 0;
        for (final int e : expected) {
            int current = -1;
            if (existing instanceof Queue) {
                current = ((Queue<Integer>)existing).poll();
            } else if (existing instanceof List) {
                current = ((List<Integer>)existing).get(pos);
            }
            assertEquals("Queue value problem at pos[" + pos++ + "]", e, current);
        }
    }

    private class LessThanComparator implements Comparator<Integer>, Serializable {
        public int compare(final Integer i1, final Integer i2) {
            if (i1.equals(i2)) {
                return 0;
            } else if (i1 < i2) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private class GreaterThanComparator implements Comparator<Integer>, Serializable {
        public int compare(final Integer i1, final Integer i2) {
            if (i1.equals(i2)) {
                return 0;
            } else if (i1 > i2) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
