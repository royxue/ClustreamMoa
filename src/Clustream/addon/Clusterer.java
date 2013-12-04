package Clustream.addon;

import Clustream.addon.MOAObject;
import Clustream.addon.Clustering;
import Clustream.addon.InstancesHeader;
import Clustream.addon.Measurement;
import Clustream.addon.AWTRenderable;
import Clustream.addon.OptionHandler;
import weka.core.Instance;

public interface Clusterer extends MOAObject, OptionHandler, AWTRenderable {

        public void setModelContext(InstancesHeader ih);

        public InstancesHeader getModelContext();

        public boolean isRandomizable();

        public void setRandomSeed(int s);

        public boolean trainingHasStarted();

        public double trainingWeightSeenByModel();

        public void resetLearning();

        public void trainOnInstance(Instance inst);

        public double[] getVotesForInstance(Instance inst);

        public Measurement[] getModelMeasurements();

        public Clusterer[] getSubClusterers();

        public Clusterer copy();

    public Clustering getClusteringResult();

    public boolean implementsMicroClusterer();

    public Clustering getMicroClusteringResult();
    
    public boolean keepClassLabel();

}