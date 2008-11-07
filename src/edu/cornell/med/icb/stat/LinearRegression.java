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

package edu.cornell.med.icb.stat;

/**
 * This class performs a Linear Regression. Based partly on the code found within
 * RegressionApplet.java found at  
 * http://www.math.csusb.edu/faculty/stanton/m262/regress/regress.html
 */
public class LinearRegression {
    /** The number of data points. */
    private int numberDataPoints;

    /** The sum of all data point x*x values. */
    private double sumxx;

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

    /**
     * Create a linear regression calculator.
     */
    public LinearRegression() {
        reset();
    }

    /**
     * Add a point to the linear regression calculation.
     * @param x the x of the point being added
     * @param y the y of the point being added
     */
    public void addDataPoint(final double x, final double y) {
        numberDataPoints++;
        sumx += x;
        sumy += y;
        sumxx += x * x;
        sumxy += x * y;
    }

    /**
     * Prepare for a new regression calculation.
     */
    public void reset() {
        xIntercept = Double.NaN;
        yIntercept = Double.NaN;
        slope = Double.NaN;

        numberDataPoints = 0;
        sumxx = 0;
        sumxy = 0;
        sumx = 0;
        sumy = 0;
    }

    /**
     * Run the regression. This should be done before calling
     * any of getXIntercept(), getYIntercept(), or getSlope().
     */
    public void regress() {
        if (numberDataPoints > 1) {
            final double Sxx = sumxx - sumx * sumx / numberDataPoints;
            final double Sxy = sumxy - sumx * sumy / numberDataPoints;
            slope = Sxy / Sxx;
            yIntercept = (sumy - slope * sumx) / numberDataPoints;
            xIntercept = (-yIntercept) / slope;
        }
    }

    /**
     * Obtain the x-intercept. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), or getSlope().
     * @return the x-intercept
     */
    public double getXIntercept() {
        return this.xIntercept;
    }

    /**
     * Obtain the y-intercept. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), or getSlope().
     * @return the x-intercept
     */
    public double getYIntercept() {
        return this.yIntercept;
    }

    /**
     * Obtain the slope. regress() should be called before calling
     * any of getXIntercept(), getYIntercept(), or getSlope().
     * @return the slope
     */
    public double getSlope() {
        return this.slope;
    }
}
