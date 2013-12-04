package Clustream.cern;

public class Polynomial {
	 protected Polynomial() {
     }
 
     public static double p1evl(double x, double coef[], int N) throws ArithmeticException {
         double ans; //双精度浮点类型p1evl数组,包含双精度x,对应的系数coef与整数N
 //ArithmeticException算数跳出
         ans = x + coef[0];
 
         for (int i = 1; i < N; i++) {
             ans = ans * x + coef[i];
         }
 
         return ans;//对于X的多项式求和 多项式形式x^N+coef[0]*x^(N-1).....
     }
 
     public static double polevl(double x, double coef[], int N) throws ArithmeticException {
         double ans;
         ans = coef[0];
 
         for (int i = 1; i <= N; i++) {
             ans = ans * x + coef[i];
         }
 
         return ans;//coef[0]*x^(N-1)+coef[1]*x^(N-2)......
     }
}
