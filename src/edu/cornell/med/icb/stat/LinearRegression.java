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

package edu.cornell.med.icb.stat;

import java.util.concurrent.locks.ReentrantLock;

/**
 * This class performs a Linear Regression. It is based on the formula from
 * http://phoenix.phys.clemson.edu/tutorials/regression/index.html
 * This asumes the relationship between the sets of data (x and y) is linear.
 * I verified the values including correlation coefficient using Excel with the
 * method described in
 * http://phoenix.phys.clemson.edu/tutorials/excel/regression.html
 */
public class LinearRegression {

    /** Lock when doing calculations on local members variables. */
    private final ReentrantLock mathLock;

    /** The number of data points. */
    private int numberDataPoints;

    /** The sum of all data point x*x values. */
    private double sumxx;

    /** The sum of all data point y*y values. */
    private double sumyy;

    /** The sum of all data point x*y values. */
    private double sumxy;

    /** The sum of all data point x values. */
    private double sumx;

    /** The sum of all data point y values. */
    private double sumy;

    /** The x-intercept as calculated in regress(). */
    private double xIntercept;

    /** The y-intercept as calculated in regress(). */
    private double yIntercept;

    /** The slope as calculated in regress(). */
    private double slope;

    /** The correlation coefficient as calculated in regress(). */
    private double correlationCoefficient;

    /**
     * Create a linear regression calculator.
     */
    public LinearRegression() {
        mathLock = new ReentrantLock();
        reset();
    }

    /**
     * Add a point to the linear regression calculation.
     * @param x the x value of the point being added
     * @param y the y value of the point being added
     */
    public void addDataPoint(final double x, final double y) {
        try {
            mathLock.lock();
            numberDataPoints++;
            sumx += x;
            sumy += y;
            sumxx += x * x;
            sumyy += y * y;
            sumxy += x * y;
        } finally {
            mathLock.unlock();
        }
    }

    /**
     * Add a points to the linear regression calculation. Variables
     * x and y need to be the same length.
     * @param x the array of x values of the points being added
     * @param y the array of y values of the points being added
     */
    public void addDataPoints(final double[] x, final double[] y) {
        assert x.length == y.length;
        try {
            mathLock.lock();
            for (int i = 0; i < x.length; i++) {
                addDataPoint(x[i], y[i]);
            }
        } finally {
            mathLock.unlock();
        }
    }

    /**
     * Prepare for a new regression calculation.
     */
    public void reset() {
        try {
            mathLock.lock();
            xIntercept = Double.NaN;
            yIntercept = Double.NaN;
            slope = Double.NaN;
            correlationCoefficient = 0;

            numberDataPoints = 0;
            sumxx = 0;
            sumyy = 0;
            sumxy = 0;
            sumx = 0;
            sumy = 0;
        } finally {
            mathLock.unlock();
        }
    }

    /**
     * Run the regression. This should be done before calling
     * any of getXIntercept(), getYIntercept(), getSlope(), or getCorrelationCoefficient().
     */
    public void regress() {
        try {
            mathLock.lock();
            if (numberDataPoints > 1) {
                // Calculate slop, x, and y intercepts
                final double top = (numberDataPoints * sumxy) - (sumx * sumy);
                final double bottom = (numberDataPoints * sumxx) - (sumx * sumx);
                slope = top / bottom;
                yIntercept = (sumy - (slope * sumx)) / numberDataPoints;
                xIntercept = (-yIntercept) / slope;

                // Calculate correlation coefficient
                final double corTop = (numberDataPoints * sumxy) - (sumx * sumy);
                final double corBottomLeft = (numberDataPoints * sumxx) - (sumx * sumx);
                final double corBottomRight = (numberDataPoints * sumyy) - (sumy * sumy);
                correlationCoefficient = corTop / Math.sqrt(corBottomLeft * corBottomRight);
            }
        } finally {
            mathLock.unlock();
        }
    }

    /**
     * Obtain the x-intercept. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), getSlope(), or getCorrelationCoefficient().
     * @return the x-intercept
     */
    public double getXIntercept() {
        return this.xIntercept;
    }

    /**
     * Obtain the y-intercept. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), getSlope(), or getCorrelationCoefficient().
     * @return the x-intercept
     */
    public double getYIntercept() {
        return this.yIntercept;
    }

    /**
     * Obtain the slope. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), getSlope(), or getCorrelationCoefficient().
     * @return the slope
     */
    public double getSlope() {
        return this.slope;
    }

    /**
     * Obtain the correlation coefficient. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), getSlope(), or getCorrelationCoefficient().
     * @return the slope
     */
    public double getCorrelationCoefficient() {
        return this.correlationCoefficient;
    }
}
