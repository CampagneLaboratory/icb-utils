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

import it.unimi.dsi.fastutil.doubles.DoubleArraySet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectList;

/**
 * @author Fabien Campagne
 *         Date: Oct 8, 2009
 *         Time: 5:22:59 PM
 */
public abstract class PredictionStatisticCalculator {
    /**
     * The value of the threshold where the optimal statistic is obtained.
     */
    public double optimalThreshold;
    /**
     * The value of the statistic.
     */
    protected double statistic;
    /**
     * Indicates that a larger statistic represents a better predictor performance.
     */
    protected boolean highestStatisticIsBest;
    /**
     * This value indicates the value of the performance statistics that would be obtained
     * if the prediction was completely random.
     */
    protected double zero;
    /**
     * The contingency table, number of true positives.
     */
    double TP;
    /**
     * The contingency table, number of true negatives.
     */
    double TN;
    /**
     * The contingency table, number of false negatives.
     */
    double FN;
    /**
     * The contingency table, number of false positives.
     */
    double FP;

    /**
     * Evaluate the contingency table at a specific threshold. The values of TP, TN, FP, FN are populated when this method
     * returns. The parameters decisionValues and labels are not modified.
     * @param threshold
     * @param decisionValues
     * @param labels
     */
    protected void evaluateContingencyTable(final double threshold, final double[] decisionValues, final double[] labels) {
            final double[] copyOfDecisionValues = new double[decisionValues.length];
            System.arraycopy(decisionValues, 0, copyOfDecisionValues, 0, decisionValues.length);
            // make decision binary according to threshold:

            for (int i = 0; i < copyOfDecisionValues.length; i++) {
                if (copyOfDecisionValues[i] < threshold) {
                    copyOfDecisionValues[i] = 0;
                } else {
                    copyOfDecisionValues[i] = 1;
                }

            }
            int tp = 0;
            int tn = 0;
            int fn = 0;
            int fp = 0;
            for (int i = 0; i < copyOfDecisionValues.length; i++) {
                final double binaryDecision = copyOfDecisionValues[i];
                final double trueLabel = labels[i];
                if (trueLabel == 1) {
                    if (binaryDecision == 1) {
                        tp++;
                    } else {
                        fn++;
                    }
                } else { // True label=0
                    if (binaryDecision == 0) {
                        tn++;
                    } else {
                        fp++;
                    }
                }
            }
            TP = tp;
            TN = tn;
            FN = fn;
            FP = fp;
        }



    public abstract String getMeasureName();

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
    public double thresholdIndependentStatistic(final ObjectList<double[]> decisionValueList, final ObjectList<double[]> labelList) {

        // any unique decision value is a potential decision threshold:
        final DoubleSet thresholds = new DoubleArraySet();
        for (final double[] decisionValues : decisionValueList) {
            for (final double value : decisionValues) {
                thresholds.add(value);
            }
        }

        double selectedThreshold = -1;
        double optimalStatistic = highestStatisticIsBest ? Double.MIN_VALUE : Double.MAX_VALUE;
        for (final double threshold : thresholds) {

            final double statisticsValueAtThreshold = evaluateStatisticAtThreshold(threshold, decisionValueList, labelList);
            if (predictivePotential(statisticsValueAtThreshold) > predictivePotential(optimalStatistic)) {

                optimalStatistic = statisticsValueAtThreshold;
                selectedThreshold = threshold;
            //    System.out.println(String.format("optimalStatistic: %f %f", optimalStatistic, selectedThreshold));
            }
        }
        statistic = optimalStatistic;
        optimalThreshold = selectedThreshold;
        return statistic;
    }

    /**
     * Return a value that is larger when the statistics indicates a larger predictive potential. Absolute value is taken
     * because values below 'zero' indicate predictive ability as well (just anti-correlated with the correct prediction).
     * The 'zero' used here is the value of the performance metric that indicates random prediction.
     *
     * @param statisticsValueAtThreshold
     * @return
     */
    protected double predictivePotential(final double statisticsValueAtThreshold) {
        return Math.abs(statisticsValueAtThreshold - zero);
    }

    /**
     * Calculates the Matthews Correlation coefficient. Find the maximal MCC value irrespective of
     * the threshold on the decision value. All the possible thresholds on the decision value
     * are scanned and the maximum MCC values found is returned.
     *
     * @param decisionValues
     * @param labels
     * @return
     */
    public double thresholdIndependentStatistic(final double[] decisionValues, final double[] labels) {
        // any unique decision value is a potential decision threshold:
        final DoubleSet thresholds = new DoubleArraySet();
        for (final double value : decisionValues) {
            thresholds.add(value);
        }

        double selectedThreshold = -1;
        double optimalStatistic = highestStatisticIsBest ? Double.MIN_VALUE : Double.MAX_VALUE;
        for (final double threshold : thresholds) {

            final double statisticsValueAtThreshold = evaluateStatisticAtThreshold(threshold, decisionValues, labels);
            if (highestStatisticIsBest && statisticsValueAtThreshold > optimalStatistic) {
                optimalStatistic = statisticsValueAtThreshold;
                selectedThreshold = threshold;
            } else {
                if (!highestStatisticIsBest && statisticsValueAtThreshold < optimalStatistic) {
                    optimalStatistic = statisticsValueAtThreshold;
                    selectedThreshold = threshold;
                }
            }
        }
        statistic = optimalStatistic;
        optimalThreshold = selectedThreshold;
        return statistic;
    }

    public double thresholdIndependentStatisticStd(final ObjectList<double[]> decisionValueList, final ObjectList<double[]> trueLabelList) {
        final ZScoreCalculator calc = new ZScoreCalculator();

        for (int i = 0; i < decisionValueList.size(); i++) {
            final double mccSingleSplit = evaluateStatisticAtThreshold(optimalThreshold, decisionValueList.get(i), trueLabelList.get(i));
            calc.observe(mccSingleSplit);
        }
        calc.calculateStats();
        return calc.stdDev();
    }

    /**
     * Calculate the standard error of the mean of the statistic.
     * Return Std of the statistic divided by the square root of the number of observations.
     *
     * @param decisionValueList
     * @param trueLabelList
     * @return
     */

    public double thresholdIndependentStatisticSte(final ObjectList<double[]> decisionValueList, final ObjectList<double[]> trueLabelList) {

        return thresholdIndependentStatisticStd(decisionValueList, trueLabelList) / Math.sqrt(decisionValueList.size());
    }

    public double evaluateStatisticAtThreshold(final double threshold, final ObjectList<double[]> decisionValueList, final ObjectList<double[]> labelList) {
        double averageStatistic = 0;
        double count = 0;
        for (int i = 0; i < decisionValueList.size(); i++) {
            final double statisticSingleSplit = evaluateStatisticAtThreshold(threshold, decisionValueList.get(i), labelList.get(i));
            averageStatistic += statisticSingleSplit;
            count++;
        }
        return averageStatistic / count;
    }

    /**
     * Evaluate the statistic for a given decision function threshold.
     *
     * @param threshold
     * @param decisionValues
     * @param labels
     */
    public abstract double evaluateStatisticAtThreshold(final double threshold, final double[] decisionValues, final double[] labels);

}
