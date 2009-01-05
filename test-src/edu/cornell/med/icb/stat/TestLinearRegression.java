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

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test the LinearRegression class. 
 * @author Kevin Dorff
 */
public class TestLinearRegression {
    /**
     * Negative slope regression.
     */
    @Test
    public void testLinearRegression() {
        final LinearRegression reg = new LinearRegression();
        reg.addDataPoint(10, 3);
        reg.addDataPoint(20, 2);
        reg.addDataPoint(30, 1);
        reg.regress();
        assertEquals(40.0d, reg.getXIntercept());
        assertEquals(4.0d, reg.getYIntercept());
        assertEquals(-0.1d, reg.getSlope());
        assertEquals(-1.0d, reg.getCorrelationCoefficient(), 0.001d);
    }

    /**
     * Another negative slope regression, but this time check after regressing just one point,
     * then add another point but don't regress and check, finally regress and check that
     * the value is correct.
     */
    @Test
    public void testLinearRegression2() {
        final LinearRegression reg = new LinearRegression();
        reg.addDataPoint(10, 3);
        // Cannot regress with one point
        reg.regress();
        assertTrue(Double.isNaN(reg.getXIntercept()));
        assertTrue(Double.isNaN(reg.getYIntercept()));
        assertTrue(Double.isNaN(reg.getSlope()));
        reg.addDataPoint(30, 2);
        // Did not call regress() after adding the point
        assertTrue(Double.isNaN(reg.getXIntercept()));
        assertTrue(Double.isNaN(reg.getYIntercept()));
        assertTrue(Double.isNaN(reg.getSlope()));
        reg.regress();
        // Now things are correct, we can regress with just two points
        assertEquals(70.0d, reg.getXIntercept());
        assertEquals(3.5d, reg.getYIntercept());
        assertEquals(-0.05d, reg.getSlope());
        assertEquals(-1.0d, reg.getCorrelationCoefficient(), 0.001d);
    }

    /**
     * Positive slope regression.
     */
    @Test
    public void testLinearRegression3() {
        final LinearRegression reg = new LinearRegression();
        reg.addDataPoint(15, 10);
        reg.addDataPoint(20, 20);
        reg.regress();
        assertEquals(10.0d, reg.getXIntercept());
        assertEquals(-20.0d, reg.getYIntercept());
        assertEquals(2.0d, reg.getSlope());
        assertEquals(1.0d, reg.getCorrelationCoefficient(), 0.001d);
    }

    @Test
    public void testArraysOfValues() {
        final double[] x = new double[] {5.05, 6.75, 3.21, 2.66};
        final double[] y = new double[] {1.65, 26.5, -5.93, 7.96};
        final LinearRegression reg = new LinearRegression();
        reg.addDataPoints(x, y);
        reg.regress();
        assertEquals(3.018d, reg.getXIntercept(), 0.001d);
        assertEquals(-16.281d, reg.getYIntercept(), 0.001d);
        assertEquals(5.393d, reg.getSlope(), 0.001d);
        assertEquals(0.724d, reg.getCorrelationCoefficient(), 0.001d);
    }
}
