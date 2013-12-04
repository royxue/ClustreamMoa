package Clustream.addon;

import java.io.IOException;
import java.io.Reader;

import weka.core.Instance;
import weka.core.Instances;

public class InstancesHeader extends Instances {

    private static final long serialVersionUID = 1L;

    public InstancesHeader(Instances i) {
        super(i, 0);
    }

    @Override
    public boolean add(Instance i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean readInstance(Reader r) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static String getClassNameString(InstancesHeader context) {
        if (context == null) {
            return "[class]";
        }
        return "[class:" + context.classAttribute().name() + "]";
    }

    public static String getClassLabelString(InstancesHeader context,
            int classLabelIndex) {
        if ((context == null) || (classLabelIndex >= context.numClasses())) {
            return "<class " + (classLabelIndex + 1) + ">";
        }
        return "<class " + (classLabelIndex + 1) + ":"
                + context.classAttribute().value(classLabelIndex) + ">";
    }

    // is impervious to class index changes - attIndex is true attribute index
    // regardless of class position
    public static String getAttributeNameString(InstancesHeader context,
            int attIndex) {
        if ((context == null) || (attIndex >= context.numAttributes())) {
            return "[att " + (attIndex + 1) + "]";
        }
        int instAttIndex = attIndex < context.classIndex() ? attIndex
                : attIndex + 1;
        return "[att " + (attIndex + 1) + ":"
                + context.attribute(instAttIndex).name() + "]";
    }

    // is impervious to class index changes - attIndex is true attribute index
    // regardless of class position
    public static String getNominalValueString(InstancesHeader context,
            int attIndex, int valIndex) {
        if (context != null) {
            int instAttIndex = attIndex < context.classIndex() ? attIndex
                    : attIndex + 1;
            if ((instAttIndex < context.numAttributes())
                    && (valIndex < context.attribute(instAttIndex).numValues())) {
                return "{val " + (valIndex + 1) + ":"
                        + context.attribute(instAttIndex).value(valIndex) + "}";
            }
        }
        return "{val " + (valIndex + 1) + "}";
    }

    // is impervious to class index changes - attIndex is true attribute index
    // regardless of class position
    public static String getNumericValueString(InstancesHeader context,
            int attIndex, double value) {
        if (context != null) {
            int instAttIndex = attIndex < context.classIndex() ? attIndex
                    : attIndex + 1;
            if (instAttIndex < context.numAttributes()) {
                if (context.attribute(instAttIndex).isDate()) {
                    return context.attribute(instAttIndex).formatDate(value);
                }
            }
        }
        return Double.toString(value);
    }
}

