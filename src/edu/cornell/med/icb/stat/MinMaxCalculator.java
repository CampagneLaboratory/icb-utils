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

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;

/**
 * Calculates the Min/Max statistics for a feature. Each feature must be given as a list of
 * value and associated class label. For each feature, we determine the minimum and maximum
 * feature values in the positive and in the negative class. We refer to these values as
 * minL0, minL1, maxL0 and maxL1. The Min/Max statistics is defined as
 * (Math.abs(maxL1 - minL0) / Math.abs(minL1 - maxL0)). This class also supports averaging
 * the k smallest and k largest observations of the feature values to estimate minL0, minL1,
 * maxL0 and maxL1 in a more robust manner. The statistics is larger when the separation of
 * minimum and maximum values of a feature among classes suggest that the feature
 * may be predictive in a subset of the samples observed.
 *
 * @author Fabien Campagne
 * Date: Apr 1, 2008
 * Time: 4:13:33 PM
 */
public class MinMaxCalculator {
    /**
     * The logger to use.
     */
    public static final Log LOG = LogFactory.getLog(MinMaxCalculator.class);

    public static double compute(final double[] values, final double[] labels) {
        return compute(values, labels, 1);
    }

    /**
     * Compute the min/max statistics.
     *
     * @param values
     * @param labels
     * @param k
     * @return
     */
    public static double compute(final double[] values, final double[] labels, final int k) {
        final DoubleList positiveLabelValues = new DoubleArrayList();
        final DoubleList negativeLabelValues = new DoubleArrayList();
        int index = 0;
        for (final double label : labels) {
            final DoubleList list = label == 0 ? negativeLabelValues : positiveLabelValues;
            list.add(values[index]);
            index++;
        }
        // sort the values for each label:

        Collections.sort(positiveLabelValues);
        Collections.sort(negativeLabelValues);
        final double[] negativeValues = negativeLabelValues.toDoubleArray();
        final double[] positiveValues = positiveLabelValues.toDoubleArray();
        final double minL0 = minOverSortedArray(negativeValues, k);
        final double maxL0 = maxOverSortedArray(negativeValues, k);
        final double minL1 = minOverSortedArray(positiveValues, k);
        final double maxL1 = maxOverSortedArray(positiveValues, k);

        final double numerator = Math.abs(maxL1 - minL0);
        final double denominator = Math.abs(minL1 - maxL0);
        if (denominator == 0) {

            if (numerator == denominator) {
                return 1;
            } else {
                LOG.warn("Cannot evaluate min/max statistics, zero denominator for numerator= " + numerator);
            }
        }
        return (numerator / denominator);

    }

    private static double maxOverSortedArray(final double[] negativeValues) {
        return maxOverSortedArray(negativeValues, 1);
    }

    private static double minOverSortedArray(final double[] negativeValues) {
        return minOverSortedArray(negativeValues, 1);
    }

    /**
     * Return the average of the k largest values of array.
     *
     * @param array Must be sorted from smallest to largest values.
     * @param k     Number of smallest elements to average.
     * @return
     */
    private static double maxOverSortedArray(final double[] array, int k) {
        double sum = 0;
        int count = 0;
        if (k > array.length) {
            k = array.length;
        }
        for (int i = array.length - k; i < array.length; i++) {
            sum += array[i];
            count++;
        }
        assert count == k;
        return sum / count;
    }

    /**
     * Return the average of the k smallest values of array.
     *
     * @param array Must be sorted from smallest to largest values.
     * @param k     Number of largest elements to average.
     * @return
     */
    private static double minOverSortedArray(final double[] array, int k) {
        double sum = 0;
        int count = 0;
        if (k > array.length) {
            k = array.length;
        }
        for (int i = 0; i < k; i++) {
            sum += array[i];
            count++;
        }
        assert count == k;
        return sum / count;
    }

    public static void main(final String[] args) {
        final double[] values = {
                1, 2, 3, 4, 5, 6, 7, 8
        };
        final double[] labels = {
                0, 0, 0, 0, 1, 1, 1, 1
        };
        System.out.println("min/max " + compute(values, labels, 1));
    }
}
