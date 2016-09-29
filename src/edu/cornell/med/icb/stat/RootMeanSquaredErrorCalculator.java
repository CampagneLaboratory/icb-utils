/*
 * Copyright (C) 2009-2010 Institute for Computational Biomedicine,
 *                        Weill Medical College of Cornell University
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

import it.unimi.dsi.fastutil.objects.ObjectList;

/**
 * Calculates the root mean squared error (RMSE). This measure is independent of any threshold.
 * The RMSE should be used only for regression, when the decision value is expected to be on the
 * same scale as the label.
 * <p/>
 * See definition at <a href="http://cran.r-project.org/web/packages/ROCR/ROCR.pdf">
 * http://cran.r-project.org/web/packages/ROCR/ROCR.pdf</a>.
 *
 * @author Fabien Campagne
 *         Date: Oct 9, 2009
 *         Time: 3:44:20 PM
 */
public class RootMeanSquaredErrorCalculator extends PredictionStatisticCalculator {
    @Override
    public String getMeasureName() {
        return "RMSE";
    }

    public RootMeanSquaredErrorCalculator() {
        highestStatisticIsBest = true;
        zero = 0;
    }

    @Override
    public double evaluateStatisticAtThreshold(final double threshold, final double[] decisionValues, final double[] labels) {

        double meanSquareError = 0;
        for (int i = 0; i < decisionValues.length; i++) {
            final double error = decisionValues[i] - labels[i];
            meanSquareError += error * error;

        }
        meanSquareError /= (double) decisionValues.length;
        return Math.sqrt(meanSquareError);

    }

    @Override
    public double thresholdIndependentStatistic(final double[] decisionValues, final double[] labels) {
        return evaluateStatisticAtThreshold(0, decisionValues, labels);
    }

    @Override
    public double thresholdIndependentStatistic(final ObjectList<double[]> decisionValueList, final ObjectList<double[]> labelList) {
        double averageStatistic = 0;
        double count = 0;
        for (int i = 0; i < decisionValueList.size(); i++) {
            final double statisticSingleSplit = thresholdIndependentStatistic(decisionValueList.get(i), labelList.get(i));
            averageStatistic += statisticSingleSplit;
            count++;
        }
        return averageStatistic / count;
    }

}
