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

package edu.cornell.med.icb.stat;

/**
 * Calculates the Matthews Correlation coefficient.
 *
 * @author Fabien Campagne
 *         Date: Apr 23, 2008
 *         Time: 3:23:25 PM
 */
public class MatthewsCorrelationCalculator extends PredictionStatisticCalculator {
    @Override
    public String getMeasureName() {
        return "MCC";
    }

    private double mcc;

    public MatthewsCorrelationCalculator() {
        highestStatisticIsBest = true;
        zero = 0;
    }

    /**
     * Evaluate the Mathews Correlation coefficient for a given decision function threshold.
     *
     * @param threshold
     * @param decisionValues
     * @param labels
     */
    public double evaluateMCC(final double threshold, final double[] decisionValues, final double[] labels) {
        evaluateContingencyTable(threshold, decisionValues, labels);
        final double value = (TP * TN - FP * FN) /
                Math.sqrt((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN));
        return value;
    }


    @Override
    public double evaluateStatisticAtThreshold(final double threshold, final double[] decisionValues, final double[] labels) {
        return evaluateMCC(threshold, decisionValues, labels);
    }
}
