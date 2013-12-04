package Clustream.addon;

import java.util.ArrayList;
import java.util.List;


public class Measurement extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected String name;

    protected double value;

    public Measurement(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public double getValue() {
        return this.value;
    }

    public static Measurement getMeasurementNamed(String name,
            Measurement[] measurements) {
        for (Measurement measurement : measurements) {
            if (name.equals(measurement.getName())) {
                return measurement;
            }
        }
        return null;
    }

    public static void getMeasurementsDescription(Measurement[] measurements,
            StringBuilder out, int indent) {
        if (measurements.length > 0) {
            StringUtils.appendIndented(out, indent, measurements[0].toString());
            for (int i = 1; i < measurements.length; i++) {
                StringUtils.appendNewlineIndented(out, indent, measurements[i].toString());
            }

        }
    }

    public static Measurement[] averageMeasurements(Measurement[][] toAverage) {
        List<String> measurementNames = new ArrayList<String>();
        for (Measurement[] measurements : toAverage) {
            for (Measurement measurement : measurements) {
                if (measurementNames.indexOf(measurement.getName()) < 0) {
                    measurementNames.add(measurement.getName());
                }
            }
        }
        GaussianEstimator[] estimators = new GaussianEstimator[measurementNames.size()];
        for (int i = 0; i < estimators.length; i++) {
            estimators[i] = new GaussianEstimator();
        }
        for (Measurement[] measurements : toAverage) {
            for (Measurement measurement : measurements) {
                estimators[measurementNames.indexOf(measurement.getName())].addObservation(measurement.getValue(), 1.0);
            }
        }
        List<Measurement> averagedMeasurements = new ArrayList<Measurement>();
        for (int i = 0; i < measurementNames.size(); i++) {
            String mName = measurementNames.get(i);
            GaussianEstimator mEstimator = estimators[i];
            if (mEstimator.getTotalWeightObserved() > 1.0) {
                averagedMeasurements.add(new Measurement("[avg] " + mName,
                        mEstimator.getMean()));
                averagedMeasurements.add(new Measurement("[err] " + mName,
                        mEstimator.getStdDev()
                        / Math.sqrt(mEstimator.getTotalWeightObserved())));
            }
        }
        return averagedMeasurements.toArray(new Measurement[averagedMeasurements.size()]);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append(getName());
        sb.append(" = ");
        sb.append(StringUtils.doubleToString(getValue(), 3));
    }
}
