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
public class SpecificityCalculator extends PredictionStatisticCalculator {
    @Override
    public String getMeasureName() {
        return "Specificity";
    }

    public SpecificityCalculator() {
        highestStatisticIsBest = true;
    }


    /**
     * Evaluate the specificity of predictions at a given decision function threshold.
     *
     * @param threshold
     * @param decisionValues
     * @param labels
     * @return Specificity of the prediction at threshold.
     */
    public double evaluateSpecificity(final double threshold, final double[] decisionValues, final double[] labels) {
        evaluateContingencyTable(threshold, decisionValues, labels);

        double value = (TN) /
                (TN + FP);
        if (value != value) {
            // NaN
            value = 0;
        }
        return value;
    }


    @Override
    public double evaluateStatisticAtThreshold(final double threshold, final double[] decisionValues, final double[] labels) {
        return evaluateSpecificity(threshold, decisionValues, labels);
    }
}
