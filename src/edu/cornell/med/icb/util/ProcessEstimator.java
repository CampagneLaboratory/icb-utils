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

package edu.cornell.med.icb.util;

import edu.cornell.med.icb.stat.LinearRegression;
import org.apache.commons.lang.time.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assist with estimation of time remaining for a long running process with a known number of
 * units of work. This will be the most reliable for tasks where each unit of work takes
 * roughly the same amount of time to complete but uses a simple LinearRegression to help
 * estimate the time remaining.
 */
public class ProcessEstimator {
    /** The stop watch, for determining the total running time. */
    private final StopWatch stopWatch;

    /** The total number of units. */
    private final AtomicInteger totalUnits;

    /** The number of completed units. */
    private final AtomicInteger unitsCompleted;

    /** The LinearRegression used to help estimate the time remaining. */
    private final LinearRegression regressor;

    /** The LinearRegression's correlation coefficient. */
    private double correlationCoefficient;

    /**
     * Create a process estimator for a specified number of totalUnits.
     * This will start the timer immediately.
     * @param totalUnitsVal the number of totalUnits this will estimate completeion for
     */
    public ProcessEstimator(final int totalUnitsVal) {
        assert totalUnitsVal > 0;
        totalUnits = new AtomicInteger(totalUnitsVal);
        stopWatch = new StopWatch();
        stopWatch.start();
        unitsCompleted = new AtomicInteger(0);
        regressor = new LinearRegression();
    }

    /**
     * Call when a unit of work has been completed. This should be called a total
     * of totalUnits times. Returns the estimated time remaining. The first time this is
     * called it will return Long.MAX_VALUE as no estimate can be made without at least
     * two data points. The result can be nicely formatted using ICBStringUtils.millis2hms().
     * @return the estimated time remaining. The first time this is called it will
     * return Long.MAX_VALUE as no estimate can be made without at least two data points.
     */
    public long unitCompleted() {
        final int numUnits = unitsCompleted.incrementAndGet();
        final long currentTime = stopWatch.getTime();
        regressor.addDataPoint(stopWatch.getTime(), totalUnits.intValue() - numUnits);
        if (numUnits < 2) {
            return Long.MAX_VALUE;
        }
        regressor.regress();
        this.correlationCoefficient = regressor.getCorrelationCoefficient();
        final long completeAt = (long) regressor.getXIntercept();
        return completeAt - currentTime;
    }

    /**
     * Obtain the correlation coefficient. Contains a valid value after unitCompleted has
     * been called at least twice.
     * @return the linear regression's correlation coefficient
     */
    public double getCorrelationCoefficient() {
        return this.correlationCoefficient;
    }

    /**
     * Get the number of total units.
     * @return the number of total units
     */
    public int getTotalUnits() {
        return totalUnits.intValue();
    }

    /**
     * Starts the timer, clearing any elapsed time.
     */
    public void startTimer() {
        stopWatch.start();
    }

    /**
     * Suspends the timer, can be resumed with resumeTimer().
     */
    public void suspendTimer() {
        stopWatch.suspend();
    }

    /**
     * Resume the timer that was suspended with suspendTimer().
     */
    public void resumeTimer() {
        stopWatch.resume();
    }

    /**
     * Return the time milliseconds that the ProcessEstimator has been running while
     * clock was running (not suspended)..
     * This can be nicely formatted using ICBStringUtils.millis2hms().
     * @return the time ProcessEstimator has been running.
     */
    public long getTimeSpent() {
        return stopWatch.getTime();
    }

    /**
     * The number of completed units of work.
     * @return number of completed units of work
     */
    public int getUnitsCompleted() {
        return unitsCompleted.intValue();
    }
}
