/*
 * Copyright (C) 2007-2010 Institute for Computational Biomedicine,
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

import cern.jet.random.engine.RandomEngine;
import org.apache.commons.math.random.AbstractRandomGenerator;

import java.util.Random;

/**
 * An adapter to use the colt RandomEngine implementations when java.util.Random is expected.
 *
 * @author Fabien Campagne
 *         Date: Oct 12, 2007
 *         Time: 5:41:06 PM
 */
public class RandomAdapter extends Random {
    /**
     * Used during serialization.
     */
    private static final long serialVersionUID = 680027482734212120L;

    private final RandomEngine engine;
    private final AbstractRandomGenerator randomGenerator;

    @Override
    public double nextGaussian() {
        return randomGenerator.nextGaussian();
    }

    public RandomAdapter(final RandomEngine engine) {
        super();
        this.engine = engine;
        randomGenerator = new AbstractRandomGenerator() {
            @Override
            public void setSeed(final long l) {

            }

            @Override
            public double nextDouble() {
                return engine.nextDouble();
            }
        };
    }

    @Override
    protected int next(final int bits) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextInt(final int n) {
        return choose(0, n - 1);
    }

    @Override
    public boolean nextBoolean() {
        return choose(0, 1) == 0;
    }

    @Override
    public long nextLong() {
        return engine.nextLong();
    }

    @Override
    public int nextInt() {
        return engine.nextInt();
    }

    @Override
    public float nextFloat() {
        return engine.nextFloat();
    }

    @Override
    public double nextDouble() {
        return engine.nextDouble();
    }

    /**
     * @param hi upper limit of range
     * @return a random integer in the range 1,2,... ,
     *         <STRONG>hi</STRONG>
     */
    public int choose(final int hi) {
        return choose(1, hi);
    }

    /**
     * @param lo lower limit of range
     * @param hi upper limit of range
     * @return a random integer in the range <STRONG>lo</STRONG>,
     *         <STRONG>lo</STRONG>+1, ... ,<STRONG>hi</STRONG>
     */
    public int choose(final int lo, final int hi) {
        return (int) ((long) lo + (long) ((1L + (long) hi - (long) lo) * engine.raw()));
    }
}
