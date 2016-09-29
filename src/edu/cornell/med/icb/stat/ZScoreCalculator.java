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

package edu.cornell.med.icb.stat;

/**
 * A helper class to calculate zScores for observations. Call observe(x) for
 * each value is needed, then call calculateStats(), then call zScore(x) again
 * to read the zScore.
 *
 * @author Fabien Campagne Date: Jun 22, 2007 Time: 5:25:57 PM
 */
public class ZScoreCalculator {
    private boolean statsReady;

    private double mean;

    /**
     * Sum of the observations.
     */
    private double sumX;

    /**
     * Number of observations.
     */
    private double N;

    /**
     * Sum of the square of the observations.
     */
    private double sumXSquared;

    private float stdDev;

    private double min;

    private double max;

    public ZScoreCalculator() {
        super();
        reset();
    }

    public void observe(final double x) {
        sumX += x;
        sumXSquared += x * x;
        ++N;
        if (x < min) {
            min = x;
        }
        if (x > max) {
            max = x;
        }
    }

    /**
     * Calculate mean and std deviation of scores.
     */
    public final void calculateStats() {
        if (statsReady) {
            return;
        }
        assert N != 0 : "You must call observe at least once before calculating statistics.";
        mean = sumX / N;

        // calculate standard deviation
        stdDev = (float) Math.sqrt(sumXSquared / N - mean * mean);
        statsReady = true;

        //System.out.println("Mean: " + mean);
        //System.out.println("Sum: " + sumXSquared);
        //System.out.println("N: " + N);

    }

    public double zScore(final double x) {
        return zScore(x, false);
    }

    public double zScore(final double x, final boolean useMinimumAsMean) {
        assert statsReady : "Statistics must have been calculated before this method is called";
        if (useMinimumAsMean) {
            return (x - min) / stdDev;
        } else {
            return (x - mean) / stdDev;
        }
    }

    public double stdDev() {
        assert statsReady : "Statistics must have been calculated before this method is called";
        return stdDev;
    }

    public double mean() {
        assert statsReady : "Statistics must have been calculated before this method is called";
        return mean;
    }

    public double sum() {
        return sumX;
    }

    public double count() {
        return N;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    /**
     * Reset the calculator to a state before any value have been observed.
     */
    public void reset() {
        N = 0;
        mean = 0;
        sumX = 0;
        sumXSquared = 0;
        statsReady = false;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
    }
}
