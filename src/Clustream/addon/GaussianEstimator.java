package Clustream.addon;

import Clustream.addon.AbstractMOAObject;

public class GaussianEstimator extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected double weightSum;

    protected double mean;

    protected double varianceSum;

    public static final double NORMAL_CONSTANT = Math.sqrt(2 * Math.PI);

    public void addObservation(double value, double weight) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return;
        }
        if (this.weightSum > 0.0) {
            this.weightSum += weight;
            double lastMean = this.mean;
            this.mean += (value - lastMean) / this.weightSum;
            this.varianceSum += (value - lastMean) * (value - this.mean);
        } else {
            this.mean = value;
            this.weightSum = weight;
        }
    }

    public void addObservations(GaussianEstimator obs) {
        if ((this.weightSum > 0.0) && (obs.weightSum > 0.0)) {
            this.mean = (this.mean * (this.weightSum / (this.weightSum + obs.weightSum)))
                    + (obs.mean * (obs.weightSum / (this.weightSum + obs.weightSum)));
            this.weightSum += obs.weightSum;
            this.varianceSum += obs.varianceSum;
        }
    }

    public double getTotalWeightObserved() {
        return this.weightSum;
    }

    public double getMean() {
        return this.mean;
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double getVariance() {
        return this.weightSum > 1.0 ? this.varianceSum / (this.weightSum - 1.0)
                : 0.0;
    }

    public double probabilityDensity(double value) {
        if (this.weightSum > 0.0) {
            double stdDev = getStdDev();
            if (stdDev > 0.0) {
                double diff = value - getMean();
                return (1.0 / (NORMAL_CONSTANT * stdDev))
                        * Math.exp(-(diff * diff / (2.0 * stdDev * stdDev)));
            }
            return value == getMean() ? 1.0 : 0.0;
        }
        return 0.0;
    }

    public double[] estimatedWeight_LessThan_EqualTo_GreaterThan_Value(
            double value) {
        double equalToWeight = probabilityDensity(value) * this.weightSum;
        double stdDev = getStdDev();
        double lessThanWeight = stdDev > 0.0 ? weka.core.Statistics.normalProbability((value - getMean()) / stdDev)
                * this.weightSum - equalToWeight
                : (value < getMean() ? this.weightSum - equalToWeight : 0.0);
        double greaterThanWeight = this.weightSum - equalToWeight
                - lessThanWeight;
        if (greaterThanWeight < 0.0) {
            greaterThanWeight = 0.0;
        }
        return new double[]{lessThanWeight, equalToWeight, greaterThanWeight};
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }
}