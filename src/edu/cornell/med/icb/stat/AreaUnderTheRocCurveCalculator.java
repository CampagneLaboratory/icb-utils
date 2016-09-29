/*
 * Copyright (C) 2009-2010 Institute for Computational Biomedicine,
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
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Fabien Campagne
 *         Date: Oct 8, 2009
 *         Time: 5:42:26 PM
 */
public class AreaUnderTheRocCurveCalculator extends PredictionStatisticCalculator {
    @Override
    public String getMeasureName() {
        return "AUC";
    }

    /**
     * Used to log debug and informational messages.
     */
    private static final Log LOG = LogFactory.getLog(AreaUnderTheRocCurveCalculator.class);

    public AreaUnderTheRocCurveCalculator() {
        highestStatisticIsBest = true;
        zero=0.5;
    }

    @Override
    public double evaluateStatisticAtThreshold(final double threshold, final double[] decisionValues, final double[] labels) {
        return statistic;
    }

    @Override
    public double thresholdIndependentStatistic(final double[] decisionValues, final double[] labels) {
        return evaluateStatistic(decisionValues, labels);
    }

    public double evaluateStatistic(final double[] decisionValues, final double[] labels) {
        double sum = 0;
        double numPositive = 0;
        double numNegative = 0;

        final DoubleList truePositiveDecisions = new DoubleArrayList();
        final DoubleList trueNegativeDecisions = new DoubleArrayList();
        for (int i = 0; i < decisionValues.length; i++) {
            if (decisionValues[i] != decisionValues[i]) {
                // decision value is NaN:
                LOG.warn("NaN found instead of a decision value. NaN are always interpreted as wrong predictions. ");
            }
            if (labels[i] >= 0) {
                truePositiveDecisions.add(decisionValues[i]);
            } else {
                trueNegativeDecisions.add(decisionValues[i]);
            }
        }

        for (final double decisionPositive : truePositiveDecisions) {
            for (final double decisionNegative : trueNegativeDecisions) {
                sum += decisionPositive > decisionNegative ? 1 : 0;
                sum += decisionPositive == decisionNegative ? 0.5 : 0;

            }

        }

        numPositive = truePositiveDecisions.size();
        numNegative = trueNegativeDecisions.size();

        final double auc = sum / numPositive / numNegative;
        statistic=auc;
        return auc;
    }


    /**
     * Calculates the optimal statistic at any decision threshold. All the possible thresholds on the decision value
     * are scanned and the optimal statistic found is returned.
     * When highestStatisticIsBest is true, the largest statistic found at any threshold is returned. Otherwise,
     * the lowest statistic is returned.
     *
     * @param decisionValueList Each element of this list should corresponds to a split of evaluation (decision values).
     * @param labelList         Each element of this list should corresponds to a split of evaluation (true labels).
     * @return
     */
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

    @Override
    public double thresholdIndependentStatisticStd(final ObjectList<double[]> decisionValueList, final ObjectList<double[]> trueLabelList) {
        final ZScoreCalculator calc = new ZScoreCalculator();

        for (int i = 0; i < decisionValueList.size(); i++) {
            final double aucSingleSplit = evaluateStatistic(decisionValueList.get(i), trueLabelList.get(i));
            calc.observe(aucSingleSplit);
        }
        calc.calculateStats();
        return calc.stdDev();
    }


}
