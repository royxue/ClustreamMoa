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


//kernels 内核即是簇

public class clustream extends AbstractClusterer{   //extends   //clustream 类 继承 AbstractClusterer
	 
    private static final long serialVersionUID = 1L;  //系列版本user identification  1L

    public IntOption timeWindowOption = new IntOption("horizon",
                    'h', "Rang of the window.", 1000);                     //时间窗选项 - - -某段时间

    public IntOption maxNumKernelsOption = new IntOption(
                    "maxNumKernels", 'k',
                    "Maximum number of micro kernels to use.", 100);     //最大数微簇选项

    public IntOption kernelRadiFactorOption = new IntOption(             //微簇半径因素选项
                    "kernelRadiFactor", 't',
                    "Multiplier for the ker'nel radius", 2);
//定义各个量
    private int timeWindow;                 //时间窗
    private long timestamp = -1;            //时间戳 初始定义-1
    private ClustreamKernel[] kernels;      //定义内核kernels
    private boolean initialized;            //初始化
    private List<ClustreamKernel> buffer; // Buffer for initialization with kNN kNN启动缓存区
    private int bufferSize;   //缓存区大小
    private double t;          
    private int m;	          


    public clustream() {
    }

//重置聚类学习器
    @Override
    public void resetLearningImpl() {     
            this.kernels = new ClustreamKernel[maxNumKernelsOption.getValue()];//定义内核
            this.timeWindow = timeWindowOption.getValue();//定义时间窗
            this.initialized = false;  //不初始化
            this.buffer = new LinkedList<ClustreamKernel>(); //缓存区定义成链表
            this.bufferSize = maxNumKernelsOption.getValue(); //缓存区
            t = kernelRadiFactorOption.getValue();  //t
            m = maxNumKernelsOption.getValue();     //m
    }
    
//初始化,在开始的数据实例中随机选取簇中心
    @Override
    public void trainOnInstanceImpl(Instance instance) {  //用来一个训练新实例的方法
            int dim = instance.numValues();    //dim实例的数目
            timestamp++;     //时间戳＋1,从0开始初始化
            // 0. Initialize
            if ( !initialized ) { 
                    if ( buffer.size() < bufferSize ) { //buffersize小于某设定值
                            buffer.add( new ClustreamKernel(instance,dim, timestamp, t, m) );  //增加缓存区
                            return;
                    }

                    int k = kernels.length;   //k为内核的长度
                    assert (k < bufferSize);  //assert用法回去补

                    ClustreamKernel[] centers = new ClustreamKernel[k];   //定义clustream内核中心
                    for ( int i = 0; i < k; i++ ) {
                            centers[i] = buffer.get( i ); // TODO: make random!
                    }
                    Clustering kmeans_clustering = kMeans(k, centers, buffer);//定义k均值算法
//                      Clustering kmeans_clustering = kMeans(k, buffer);

                    for ( int i = 0; i < kmeans_clustering.size(); i++ ) {
                            kernels[i] = new ClustreamKernel( new DenseInstance(1.0,centers[i].getCenter()), dim, timestamp, t, m );
                    }   //内核赋值

                    buffer.clear();//清空缓存区
                    initialized = true;//开始初始化
                    return;
            }


            // 根据距离判断最近的簇 
            ClustreamKernel closestKernel = null;//定义最近的核心量
            double minDistance = Double.MAX_VALUE;//定义最短距离.(double.max_value?)
            for ( int i = 0; i < kernels.length; i++ ) {
                    //System.out.println(i+" "+kernels[i].getWeight()+" "+kernels[i].getDeviation());
                    double distance = distance(instance.toDoubleArray(), kernels[i].getCenter() );
                    if ( distance < minDistance ) {
                            closestKernel = kernels[i];
                            minDistance = distance;
                    }
            }

            // 检查实例是否可以放入最近的簇
            double radius = 0.0;
            if ( closestKernel.getWeight() == 1 ) {
                    //特殊情况:可以通过通过确定到下一个最近簇的距离来估计半径
                    radius = Double.MAX_VALUE; //double的最大可能数值
                    double[] center = closestKernel.getCenter();  // 获得最大值
                    for ( int i = 0; i < kernels.length; i++ ) {
                            if ( kernels[i] == closestKernel ) {
                                    continue;
                            }

                            double distance = distance(kernels[i].getCenter(), center );
                            radius = Math.min( distance, radius );   //半径取距离和半径的最小值
                    }
            } else {
                    radius = closestKernel.getRadius();
            }

            if ( minDistance < radius ) {
                    //若数据满足条件可以放进簇,将实例插入最近簇中(实例,时间戳)
                    closestKernel.insert( instance, timestamp );
                    return;
            }

           //如果数据无法放入最近的簇,则需要通过删除旧簇来为新簇的插入释放空间

            long threshold = timestamp - timeWindow; //用时间作为在此之前的微簇可以舍弃

            //删除旧簇  
            for ( int i = 0; i < kernels.length; i++ ) {
                    if ( kernels[i].getRelevanceStamp() < threshold ) {   //取相关标记对比
                            kernels[i] = new ClustreamKernel( instance, dim, timestamp, t, m );//建立新簇
                            return;
                    }
            }

            //将两个距离最近的簇合并
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

            kernels[closestA].add( kernels[closestB] ); //ab合并
            kernels[closestB] = new ClustreamKernel( instance, dim, timestamp, t,  m ); //b用于生成新簇
    }

  //一个种用来获得现在微聚类结果（用来评估或者可视化）的方法
    @Override
    public Clustering getMicroClusteringResult() {   
            if ( !initialized ) {
                    return new Clustering( new Cluster[0] );  //未初始化,返回0结果
            }

            ClustreamKernel[] res = new ClustreamKernel[kernels.length];
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] = new ClustreamKernel( kernels[i], t, m );   //利用新的簇队列res来返回聚类结果
            }

            return new Clustering( res );
    }

    @Override
    public boolean implementsMicroClusterer() {    //微簇
            return true;
    }
//@override
    public Clustering getClusteringResult() {  //获得聚类结果
            return null;
    }

    public String getName() {
            return "Clustream " + timeWindow; //聚类结果name "Clustream " + timeWindow
    }

    //距离.两件间距平方和开根号
    private static double distance(double[] pointA, double [] pointB){  //距离定义,点间距距离平方和
            double distance = 0.0;
            for (int i = 0; i < pointA.length; i++) {
                    double d = pointA[i] - pointB[i];
                    distance += d * d;
            }
            return Math.sqrt(distance);   //平方和的开根号
    }

    //Kmeans方法
    public static Clustering kMeans( int k, List<? extends Cluster> data ) {
            Random random = new Random(0); //随机
            Cluster[] centers = new Cluster[k];   //簇中心
            for (int i = 0; i < centers.length; i++) {
                    int rid = random.nextInt(k);    //k=>随机值
                    centers[i] = new SphereCluster(data.get(rid).getCenter(),0);  //取随机数据的中心
            }
            Clustering clustering = kMeans(k, centers, data);  //k,中心.数据
            return clustering;
    }

    //Kmeans方法
    public static Clustering kMeans( int k, Cluster[] centers, List<? extends Cluster> data ) {
            assert (centers.length == k);
            assert (k > 0);

            int dimensions = centers[0].getCenter().length; //维度=>簇中心长度

            ArrayList<ArrayList<Cluster>> clustering = new ArrayList<ArrayList<Cluster>>();
            for ( int i = 0; i < k; i++ ) {
                    clustering.add( new ArrayList<Cluster>() );
            }

            int repetitions = 100;   //设定重复次数100次
            while ( repetitions-- >= 0 ) {
                    // 把点分配给簇
                    for ( Cluster point : data ) {
                            double minDistance = distance( point.getCenter(), centers[0].getCenter() );   //最小距离点与第一簇的距离
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
					//依次循环比较点与中心的距离,找出最近的簇类似于冒泡法,找出后将点加入簇

                    // 计算新的中心,清空表单
                    SphereCluster[] newCenters = new SphereCluster[centers.length];  //sphere范围,球型= =此处判定为范围簇
                    for ( int i = 0; i < k; i++ ) {
                            newCenters[i] = calculateCenter( clustering.get( i ), dimensions );
                            clustering.get( i ).clear();
                    }
                    centers = newCenters;
            }

            return new Clustering( centers );     //返回新的簇中心
    }
//计算中心
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

            // 标准化
            for ( int i = 0; i < res.length; i++ ) {
                    res[i] /= cluster.size();
            }

            //计算半径
            double radius = 0.0;
            for ( Cluster point : cluster ) {
                    double dist = distance( res, point.getCenter() );   //res结果簇
                    if ( dist > radius ) {
                            radius = dist;
                    }
            }
            SphereCluster sc = new SphereCluster( res, radius );
            sc.setWeight(cluster.size());
            return sc;
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {  //获得模型评估
            throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {   //获得模型描述
            throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRandomizable() {   //是否随机
            return false;
    }

    public double[] getVotesForInstance(Instance inst) {  //实例投票
            throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public static void main(String[] args)
    {
    	System.out.println("Just say yes");
    }


}

