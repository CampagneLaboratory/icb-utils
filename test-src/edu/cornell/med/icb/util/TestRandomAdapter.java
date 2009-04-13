/*
 * Copyright (C) 2007-2009 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.util;

import cern.jet.random.engine.MersenneTwister;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import junit.framework.TestCase;

/**
 * Test that the RandomAdapter "choose" method returns
 * the appropriate values. If you say "choose(2,5)" it should
 * return back random values 2..5 inclusive, ie, 2, 3, 4, 5.
 * These tests verify that the right values are returned and
 * that given enough requests, the entire window of values
 * are returned. It is POSSIBLE that the test will fail,
 * which would mean that in 2000 requests all possible
 * values weren't all returned, but it is very unlikely.
 * In the unlikely event that this this test DOES fail,
 * try running it again.
 * @author Kevin C. Dorff, Dec 12 2007
 */
public class TestRandomAdapter extends TestCase {
    public void testChoseOneArg() {
        final RandomAdapter random = new RandomAdapter(new MersenneTwister());
        final IntSet ints = new IntArraySet(5);
        for (int i = 0; i < 2000; i++) {
            final int value = random.choose(5);
            assertTrue("Value must be 1 .. 5 but was " + value, value >= 1 && value <= 5);
            ints.add(value);
        }
        assertEquals("Should have been five values that were returned", 5, ints.size());
    }

    public void testChoseTwoArg() {
        final RandomAdapter random = new RandomAdapter(new MersenneTwister());
        final IntSet ints = new IntArraySet(5);
        for (int i = 0; i < 2000; i++) {
            final int value = random.choose(2, 6);
            assertTrue("Value must be 2 .. 6 but was " + value, value >= 2 && value <= 6);
            ints.add(value);
        }
        assertEquals("Should have been five values that were returned", 5, ints.size());
    }
}
