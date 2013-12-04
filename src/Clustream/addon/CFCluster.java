package Clustream.addon;

import java.util.Arrays;
import weka.core.Instance;

public abstract class CFCluster extends SphereCluster {
	 
    private static final long serialVersionUID = 1L;

    protected double radiusFactor = 1.8;

    protected double N;
    public double[] LS;
    public double[] SS;

    public CFCluster(Instance instance, int dimensions) {
            this(instance.toDoubleArray(), dimensions);
    }

    protected CFCluster(int dimensions) {
            this.N = 0;
            this.LS = new double[dimensions];
            this.SS = new double[dimensions];
            Arrays.fill(this.LS, 0.0);
            Arrays.fill(this.SS, 0.0);
    }

    public CFCluster(double [] center, int dimensions) {
            this.N = 1;
            this.LS = center;
            this.SS = new double[dimensions];
            for (int i = 0; i < SS.length; i++) {
                    SS[i]=Math.pow(center[i], 2);
            }
    }

    public CFCluster(CFCluster cluster) {
            this.N = cluster.N;
            this.LS = Arrays.copyOf(cluster.LS, cluster.LS.length);
            this.SS = Arrays.copyOf(cluster.SS, cluster.SS.length);
    }

    public void add(CFCluster cluster ) {
            this.N += cluster.N;
            addVectors( this.LS, cluster.LS );
            addVectors( this.SS, cluster.SS );
    }

    public abstract CFCluster getCF();

     @Override
     public double[] getCenter() {
             assert (this.N>0);
             double res[] = new double[this.LS.length];
             for ( int i = 0; i < res.length; i++ ) {
                     res[i] = this.LS[i] / N;
             }
             return res;
     }


     @Override
     public abstract double getInclusionProbability(Instance instance);

     @Override
     public abstract double getRadius();

     @Override
     public double getWeight() {
             return N;
     }

     public void setN(double N){
             this.N = N;
     }

     public double getN() {
             return N;
     }

     public static void addVectors(double[] a1, double[] a2) {
             assert (a1 != null);
             assert (a2 != null);
             assert (a1.length == a2.length) : "Adding two arrays of different "
                     + "length";

             for (int i = 0; i < a1.length; i++) {
                     a1[i] += a2[i];
             }
     }
}
