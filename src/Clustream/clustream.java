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


//kernels �ں˼��Ǵ�

public class clustream extends AbstractClusterer{   //extends   //clustream �� �̳� AbstractClusterer
	 
    private static final long serialVersionUID = 1L;  //ϵ�а汾user identification  1L

    public IntOption timeWindowOption = new IntOption("horizon",
                    'h', "Rang of the window.", 1000);                     //ʱ�䴰ѡ�� - - -ĳ��ʱ��

    public IntOption maxNumKernelsOption = new IntOption(
                    "maxNumKernels", 'k',
                    "Maximum number of micro kernels to use.", 100);     //�����΢��ѡ��

    public IntOption kernelRadiFactorOption = new IntOption(             //΢�ذ뾶����ѡ��
                    "kernelRadiFactor", 't',
                    "Multiplier for the ker'nel radius", 2);
//���������
    private int timeWindow;                 //ʱ�䴰
    private long timestamp = -1;            //ʱ��� ��ʼ����-1
    private ClustreamKernel[] kernels;      //�����ں�kernels
    private boolean initialized;            //��ʼ��
    private List<ClustreamKernel> buffer; // Buffer for initialization with kNN kNN����������
    private int bufferSize;   //��������С
    private double t;          
    private int m;	          


    public clustream() {
    }

//���þ���ѧϰ��
    @Override
    public void resetLearningImpl() {     
            this.kernels = new ClustreamKernel[maxNumKernelsOption.getValue()];//�����ں�
            this.timeWindow = timeWindowOption.getValue();//����ʱ�䴰
            this.initialized = false;  //����ʼ��
            this.buffer = new LinkedList<ClustreamKernel>(); //���������������
            this.bufferSize = maxNumKernelsOption.getValue(); //������
            t = kernelRadiFactorOption.getValue();  //t
            m = maxNumKernelsOption.getValue();     //m
    }
    
//��ʼ��,�ڿ�ʼ������ʵ�������ѡȡ������
    @Override
    public void trainOnInstanceImpl(Instance instance) {  //����һ��ѵ����ʵ���ķ���
            int dim = instance.numValues();    //dimʵ������Ŀ
            timestamp++;     //ʱ�����1,��0��ʼ��ʼ��
            // 0. Initialize
            if ( !initialized ) { 
                    if ( buffer.size() < bufferSize ) { //buffersizeС��ĳ�趨ֵ
                            buffer.add( new ClustreamKernel(instance,dim, timestamp, t, m) );  //���ӻ�����
                            return;
                    }

                    int k = kernels.length;   //kΪ�ں˵ĳ���
                    assert (k < bufferSize);  //assert�÷���ȥ��

                    ClustreamKernel[] centers = new ClustreamKernel[k];   //����clustream�ں�����
                    for ( int i = 0; i < k; i++ ) {
                            centers[i] = buffer.get( i ); // TODO: make random!
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


            // ���ݾ����ж�����Ĵ� 
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

            // ���ʵ���Ƿ���Է�������Ĵ�
            double radius = 0.0;
            if ( closestKernel.getWeight() == 1 ) {
                    //�������:����ͨ��ͨ��ȷ������һ������صľ��������ư뾶
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
                    //�����������������ԷŽ���,��ʵ�������������(ʵ��,ʱ���)
                    closestKernel.insert( instance, timestamp );
                    return;
            }

           //��������޷���������Ĵ�,����Ҫͨ��ɾ���ɴ���Ϊ�´صĲ����ͷſռ�

            long threshold = timestamp - timeWindow; //��ʱ����Ϊ�ڴ�֮ǰ��΢�ؿ�������

            //ɾ���ɴ�  
            for ( int i = 0; i < kernels.length; i++ ) {
                    if ( kernels[i].getRelevanceStamp() < threshold ) {   //ȡ��ر�ǶԱ�
                            kernels[i] = new ClustreamKernel( instance, dim, timestamp, t, m );//�����´�
                            return;
                    }
            }

            //��������������Ĵغϲ�
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

  //һ���������������΢�������������������߿��ӻ����ķ���
    @Override
    public Clustering getMicroClusteringResult() {   
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

    //����.�������ƽ���Ϳ�����
    private static double distance(double[] pointA, double [] pointB){  //���붨��,�������ƽ����
            double distance = 0.0;
            for (int i = 0; i < pointA.length; i++) {
                    double d = pointA[i] - pointB[i];
                    distance += d * d;
            }
            return Math.sqrt(distance);   //ƽ���͵Ŀ�����
    }

    //Kmeans����
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

    //Kmeans����
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
                    // �ѵ�������
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

                    // �����µ�����,��ձ�
                    SphereCluster[] newCenters = new SphereCluster[centers.length];  //sphere��Χ,����= =�˴��ж�Ϊ��Χ��
                    for ( int i = 0; i < k; i++ ) {
                            newCenters[i] = calculateCenter( clustering.get( i ), dimensions );
                            clustering.get( i ).clear();
                    }
                    centers = newCenters;
            }

            return new Clustering( centers );     //�����µĴ�����
    }
//��������
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

            // ��׼��
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] /= cluster.size();
            }

            //����뾶
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
    
    
    public static void main(String[] args)
    {
    	System.out.println("Just say yes");
    }


}

