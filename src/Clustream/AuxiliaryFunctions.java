package Clustream;
import Clustream.cern.Gamma;

public class AuxiliaryFunctions {  //辅助方程
	 public static final int GAMMA_ITERATIONS = 100;//Gamma迭代次数100
     
	 
     private static double incompleteGamma(double a, double x) {
         assert (!Double.isNaN(a));//assert验证程序的正确性
         assert (!Double.isNaN(x));
         double sum = 0.0;   //定义和
 
         double xPow = 1; // Start with x^0
         for (int n = 0; n < GAMMA_ITERATIONS; n++) {
             double denom = a;
             for (int i = 1; i <= n; i++) {
                 denom *= (a + i);
             }
 
             assert (denom != 0.0);
             sum += xPow / denom;
             xPow *= x;
         }
 
         double res = Math.pow(x, a) * Math.exp(-x) * sum;
 
         if (Double.isNaN(res)) {
             System.err.println("a " + a);
             System.err.println("x " + x);
             System.err.println("x^a " + Math.pow(x, a));
             System.err.println("e^-x " + Math.exp(-x));
             System.err.println("sum " + sum);
             assert (false);
         }
 
         return res;
     }
 
     public static double gammaHalf(int n) {
         int[] doubleFac = new int[]{1, 1, 2, 3, 8, 15, 48, 105, 384, 945, 3840,
             10395, 46080, 135135, 645120, 2027025,
             10321920, 34459425, 185794560, 654729075};
 
         if (n == 0) {
             return Double.POSITIVE_INFINITY;
         }
 
         // Integers are simple fac(n)
         if ((n % 2) == 0) {
             int v = (n / 2) - 1;
             int res = 1;
 
             for (int i = 1; i <= v; i++) {
                 res *= i;
             }
 
             return res;
         }
 
         // First two would yeald negative double factorials
         if (n == 1) {
             return 1.0692226492664116 / 0.6032442812094465;
         }
         if (n == 3) {
             return 0.947573901083827 / 1.0692226492664116;
         }
 
         return Math.sqrt(Math.PI) * (doubleFac[n - 2]) / (Math.pow(2, (n - 1) * .5));
     }
 
     public static double distanceProbabilty(double threshold, int dimension) {
         // threshold = (threshold*threshold) * .5;
         if (threshold == 0) {
             return 1;
         }
 
         //return 1 - (incompleteGamma(dimension * .5, threshold) / gammaHalf(dimension));
         return 1 - (Gamma.incompleteGamma(dimension * .5, threshold * .5)
                 / gammaHalf(dimension));
     }
 
     public static double gompertzWeight(double average, double count) {
         if ( average < 2.0 )
             return 1.0;
 
         double logT = Math.log(0.97);
         double logt = Math.log(0.0001);
 
         double denomB = (Math.pow(logT * logT, 1 / (average - 2.0)));
 
         double b = (Math.pow(logt * logt, 1.0 / (2.0 * (1.0 - (2.0 / average))))) / denomB;
         double c = -(1.0 / average) * Math.log(-(1.0 / b) * logT);
 
         assert (b >= 0) : "Bad b " + b + ", average " + average;
         assert (c >= 0) : "Bad c " + c + ", average " + average;
 
         // Should be okay, the following test fails for some numerica
         // bad examples
         // assert (Math.exp(-b * Math.exp(-c * average)) > 0.95);
         // assert (Math.exp(-b * Math.exp(-c * 2)) < 0.001);
 
         return Math.exp(-b * Math.exp(-c * count));
     }
 
	
}
