package Clustream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import Clustream.addon.Cluster;
import Clustream.addon.Clustering;
import Clustream.addon.IntOption;
import Clustream.addon.SphereCluster;
import Clustream.addon.AbstractClusterer;
import Clustream.addon.Measurement;
import weka.core.DenseInstance;
import weka.core.Instance;


//kernels => clusters
//instance => data point

public class myClustream extends AbstractClusterer{    //abstractClusterer
	 
    private static final long serialVersionUID = 1L;  //ϵ�а汾user identification  1L

    public IntOption timeWindowOption = new IntOption("horizon",
                    'h', "Rang of the window.", 1000);                     //Time option

    public IntOption maxNumKernelsOption = new IntOption(
                    "maxNumKernels", 'k',
                    "Maximum number of micro kernels to use.", 100);     //max number of kernels

    public IntOption kernelRadiFactorOption = new IntOption(             //radius of kernels
                    "kernelRadiFactor", 't',
                    "Multiplier for the ker'nel radius", 2);

    private int timeWindow;                 //time
    private long timestamp = -1;            //
    private ClustreamKernel[] kernels;      // clustreamKernel
    private boolean initialized;            // is it initialized?
    private List<ClustreamKernel> buffer; //  buffer => list
    private int bufferSize;   //
    private double t;         //t 
    private int m;	          //m 


    public myClustream() {
    }
    
    
    public static void main (String[] args)
    {
    	System.out.println("Start Clustream");
    	
    	
    }


    @Override
    public void resetLearningImpl() {      //reset
            this.kernels = new ClustreamKernel[maxNumKernelsOption.getValue()];//set kernels
            this.timeWindow = timeWindowOption.getValue();//set time
            this.initialized = false;  //havent initialized
            this.buffer = new LinkedList<ClustreamKernel>(); //buffer => linked list
            this.bufferSize = maxNumKernelsOption.getValue(); //bufferSize = kernelsNum
            t = kernelRadiFactorOption.getValue();  //t
            m = maxNumKernelsOption.getValue();     //m
    }
    
    //Set Impl with option from user set

    @Override
    public void trainOnInstanceImpl(Instance instance) {  
            int dim = instance.numValues();    //dim => num of dimensions
            timestamp++;     //timestamp = 0,ready to start
            // 0. Initialize
            if ( !initialized ) { 
                    if ( buffer.size() < bufferSize ) { //kernels is not enough
                            buffer.add( new ClustreamKernel(instance,dim, timestamp, t, m) );  //���ӻ�����
                            return;
                    }

                    int k = kernels.length;   //
                    assert (k < bufferSize);  //if true output true

                    ClustreamKernel[] centers = new ClustreamKernel[k];   //cluster center
                    for ( int i = 0; i < k; i++ ) {
                            centers[i] = buffer.get( i ); // TODO: make random!�趨�������
                    }
                    Clustering kmeans_clustering = kMeans(k, centers, buffer);//����k��ֵ�㷨
//                      Clustering kmeans_clustering = kMeans(k, buffer);

                    for ( int i = 0; i < kmeans_clustering.size(); i++ ) {
                            kernels[i] = new ClustreamKernel( new DenseInstance(1.0,centers[i].getCenter()), dim, timestamp, t, m );
                    }   //�ں˸�ֵ

                    buffer.clear();//��ջ�����
                    initialized = true;//��ʼ��ʼ��
                    return;
            }


            // Step 1. Determine closest kernel ��������ĺ��� 
            ClustreamKernel closestKernel = null;//��������ĺ�����
            double minDistance = Double.MAX_VALUE;//������̾���.(double.max_value?)
            for ( int i = 0; i < kernels.length; i++ ) {
                    //System.out.println(i+" "+kernels[i].getWeight()+" "+kernels[i].getDeviation());
                    double distance = distance(instance.toDoubleArray(), kernels[i].getCenter() );
                    if ( distance < minDistance ) {
                            closestKernel = kernels[i];
                            minDistance = distance;
                    }
            }

            // 2. Check whether instance fits into closestKernel  //�鿴ʵ���Ƿ��ʺ�������ں�
            double radius = 0.0;
            if ( closestKernel.getWeight() == 1 ) {
                    // Special case: estimate radius by determining the distance to the   ͨ��ȷ������һ������صľ��������ư뾶
                    // next closest cluster
                    radius = Double.MAX_VALUE; //double����������ֵ
                    double[] center = closestKernel.getCenter();  // ������ֵ
                    for ( int i = 0; i < kernels.length; i++ ) {
                            if ( kernels[i] == closestKernel ) {
                                    continue;
                            }

                            double distance = distance(kernels[i].getCenter(), center );
                            radius = Math.min( distance, radius );   //�뾶ȡ����Ͱ뾶����Сֵ
                    }
            } else {
                    radius = closestKernel.getRadius();
            }

            if ( minDistance < radius ) {
                    // Date fits, put into kernel and be happy  ��������,��ʵ�������������(ʵ��,ʱ���)
                    closestKernel.insert( instance, timestamp );
                    return;
            }

            // 3. Date does not fit, we need to free
            // some space to insert a new kernel                    //�����Ҳ������ʵ�΢��,����Ҫͨ��ɾ���ɴ���Ϊ�´صĲ����ͷſռ�

            long threshold = timestamp - timeWindow; // Kernels before this can be forgotten  �ڴ�֮ǰ��΢�ؿ�������

            // 3.1 Try to forget old kernels  
            for ( int i = 0; i < kernels.length; i++ ) {
                    if ( kernels[i].getRelevanceStamp() < threshold ) {   //ȡ��ر�ǶԱ�
                            kernels[i] = new ClustreamKernel( instance, dim, timestamp, t, m );//�����´�
                            return;
                    }
            }

            // 3.2 Merge closest two kernels  �ϲ��ز������´�
            int closestA = 0;
            int closestB = 0;
            minDistance = Double.MAX_VALUE;
            for ( int i = 0; i < kernels.length; i++ ) {
                    double[] centerA = kernels[i].getCenter();
                    for ( int j = i + 1; j < kernels.length; j++ ) {
                            double dist = distance( centerA, kernels[j].getCenter() );
                            if ( dist < minDistance ) {
                                    minDistance = dist;
                                    closestA = i;
                                    closestB = j;
                            }
                    }
            }
            assert (closestA != closestB);

            kernels[closestA].add( kernels[closestB] ); //ab�ϲ�
            kernels[closestB] = new ClustreamKernel( instance, dim, timestamp, t,  m ); //b���������´�
    }

    @Override
    public Clustering getMicroClusteringResult() {   //һ���������������΢�������������������߿��ӻ����ķ���
            if ( !initialized ) {
                    return new Clustering( new Cluster[0] );  //δ��ʼ��,����0���
            }

            ClustreamKernel[] res = new ClustreamKernel[kernels.length];
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] = new ClustreamKernel( kernels[i], t, m );   //�����µĴض���res�����ؾ�����
            }

            return new Clustering( res );
    }

    @Override
    public boolean implementsMicroClusterer() {    //΢��
            return true;
    }
//@override
    public Clustering getClusteringResult() {  //��þ�����
            return null;
    }

    public String getName() {
            return "Clustream " + timeWindow; //������name "Clustream " + timeWindow
    }

    private static double distance(double[] pointA, double [] pointB){  //���붨��,�������ƽ����
            double distance = 0.0;
            for (int i = 0; i < pointA.length; i++) {
                    double d = pointA[i] - pointB[i];
                    distance += d * d;
            }
            return Math.sqrt(distance);   //ƽ���͵Ŀ�����
    }

    public static Clustering kMeans( int k, List<? extends Cluster> data ) {
            Random random = new Random(0); //���
            Cluster[] centers = new Cluster[k];   //������
            for (int i = 0; i < centers.length; i++) {
                    int rid = random.nextInt(k);    //k=>���ֵ
                    centers[i] = new SphereCluster(data.get(rid).getCenter(),0);  //ȡ������ݵ�����
            }
            Clustering clustering = kMeans(k, centers, data);  //k,����.����
            return clustering;
    }

    public static Clustering kMeans( int k, Cluster[] centers, List<? extends Cluster> data ) {
            assert (centers.length == k);
            assert (k > 0);

            int dimensions = centers[0].getCenter().length; //ά��=>�����ĳ���

            ArrayList<ArrayList<Cluster>> clustering = new ArrayList<ArrayList<Cluster>>();
            for ( int i = 0; i < k; i++ ) {
                    clustering.add( new ArrayList<Cluster>() );
            }

            int repetitions = 100;   //�趨�ظ�����100��
            while ( repetitions-- >= 0 ) {
                    // Assign points to clusters
                    for ( Cluster point : data ) {
                            double minDistance = distance( point.getCenter(), centers[0].getCenter() );   //��С��������һ�صľ���
                            int closestCluster = 0;
                            for ( int i = 1; i < k; i++ ) {
                                    double distance = distance( point.getCenter(), centers[i].getCenter() );
                                    if ( distance < minDistance ) {
                                            closestCluster = i;
                                            minDistance = distance;
                                    }
                            }

                            clustering.get( closestCluster ).add( point );
                    }
					//����ѭ���Ƚϵ������ĵľ���,�ҳ�����Ĵ�������ð�ݷ�,�ҳ��󽫵�����

                    // Calculate new centers and clear clustering lists   �����µ�����,��������
                    SphereCluster[] newCenters = new SphereCluster[centers.length];  //sphere��Χ,����= =�˴��ж�Ϊ��Χ��
                    for ( int i = 0; i < k; i++ ) {
                            newCenters[i] = calculateCenter( clustering.get( i ), dimensions );
                            clustering.get( i ).clear();
                    }
                    centers = newCenters;
            }

            return new Clustering( centers );     //newCenters
    }

    private static SphereCluster calculateCenter( ArrayList<Cluster> cluster, int dimensions ) {
            double[] res = new double[dimensions];
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] = 0.0;
            }

            if ( cluster.size() == 0 ) {
                    return new SphereCluster( res, 0.0 );
            }

            for ( Cluster point : cluster ) {
                    double [] center = point.getCenter();
                    for (int i = 0; i < res.length; i++) {
                            res[i] += center[i];
                    }
            }

            // Normalize
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] /= cluster.size();
            }

            // Calculate radius  //����뾶
            double radius = 0.0;
            for ( Cluster point : cluster ) {
                    double dist = distance( res, point.getCenter() );   //res�����
                    if ( dist > radius ) {
                            radius = dist;
                    }
            }
            SphereCluster sc = new SphereCluster( res, radius );
            sc.setWeight(cluster.size());
            return sc;
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {  //���ģ������
            throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {   //���ģ������
            throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRandomizable() {   //�Ƿ����
            return false;
    }

    public double[] getVotesForInstance(Instance inst) {  //ʵ��ͶƱ
            throw new UnsupportedOperationException("Not supported yet.");
    }
    
    


}

