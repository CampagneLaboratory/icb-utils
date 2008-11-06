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

package edu.cornell.med.icb.util;

import org.apache.commons.lang.time.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assist with estimation of time remaining for a long running process with a known number of
 * units of work. This will be the most reliable for tasks where each unit of work takes
 * roughly the same amount of time to complete.
 *
 * This class takes an incredibly simplistic approach to the estimation process making the
 * assumption that each future unit of work will take the average amount of time of each
 * completed unit of work. A more complete solution would use something like Apache
 * Commons Math's SimpleRegression to help calculate the estimated remaining time.
 */
public class ProcessEstimator {
    /** The stop watch, for determining the total running time. */
    private StopWatch stopWatch = new StopWatch();

    /** The total number of units. */
    private AtomicInteger totalUnits = new AtomicInteger(0);

    /** The number of completed units. */
    private AtomicInteger unitsCompleted = new AtomicInteger(0);

    /**
     * Create a process estimator for a specified number of totalUnits.
     * This will start the timer immediately.
     * @param totalUnitsVal the number of totalUnits this will estimate completeion for
     */
    public ProcessEstimator(final int totalUnitsVal) {
        totalUnits.set(totalUnitsVal);
        stopWatch.start();
    }

    /**
     * Call when a unit of work has been completed. This should be called a total
     * of totalUnits times.
     */
    public void unitCompleted() {
        unitsCompleted.incrementAndGet();
    }

    /**
     * The project number of milliseconds remaining.
     * This can be nicely formatted using ICBStringUtils.millis2hms().
     * @return project number of milliseconds remaining
     */
    public long getEstimatedTimeRemaining() {
        final long remain = (getTotalUnits() - getUnitsCompleted())
                * getTimePerUnit();
        if (remain >= 0) {
            return remain;
        } else {
            return 0;
        }
    }

    /**
     * Add to the new number of total units (used if it changes from the constructor).
     * @param toAdd the number to add to totalUnits
     */
    public void addToTotalUnits(final int toAdd) {
        totalUnits.addAndGet(toAdd);
    }

    /**
     * Set the new number of total units (used if it changes from the constructor).
     * @param newValue the new number of total units
     */
    public void setTotalUnits(final int newValue) {
        totalUnits.set(newValue);
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

    /**
     * The average time spent per unit while the clock was running (not suspended).
     * This can be nicely formatted using ICBStringUtils.millis2hms().
     * @return average time spent per unit
     */
    public long getTimePerUnit() {
        final int numCompleted = unitsCompleted.intValue();
        final long timeSpent = getTimeSpent();
        if (timeSpent == 0 || numCompleted == 0) {
            return 0;
        }
        return timeSpent / numCompleted;
    }
}
