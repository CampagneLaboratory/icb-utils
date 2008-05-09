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

package edu.cornell.med.icb.io;

/**
 * Class to test bean stuff.
 * @author Kevin Dorff (Nov 26, 2007)
 */
public class SampleBean {
    private int oneInt;
    private double twoDouble;
    private String threeString;
    private int[] oneBeeIntArray;
    private double[] twoBeeDoubleArray;
    private String[] threeBeeStringArray;

    private SampleBean subBean;

    public int getOneInt() {
        return oneInt;
    }

    public void setOneInt(final int oneInt) {
        this.oneInt = oneInt;
    }

    public double getTwoDouble() {
        return twoDouble;
    }

    public void setTwoDouble(final double twoDouble) {
        this.twoDouble = twoDouble;
    }

    public String getThreeString() {
        return threeString;
    }

    public void setThreeString(final String threeString) {
        this.threeString = threeString;
    }

    public int[] getOneBeeIntArray() {
        return oneBeeIntArray;
    }

    public void setOneBeeIntArray(final int[] oneBeeIntArray) {
        this.oneBeeIntArray = oneBeeIntArray;
    }

    public double[] getTwoBeeDoubleArray() {
        return twoBeeDoubleArray;
    }

    public void setTwoBeeDoubleArray(final double[] twoBeeDoubleArray) {
        this.twoBeeDoubleArray = twoBeeDoubleArray;
    }

    public String[] getThreeBeeStringArray() {
        return threeBeeStringArray;
    }

    public void setThreeBeeStringArray(final String[] threeBeeStringArray) {
        this.threeBeeStringArray = threeBeeStringArray;
    }

    public SampleBean getSubBean() {
        return subBean;
    }

    public void setSubBean(final SampleBean subBean) {
        this.subBean = subBean;
    }
}
