package Clustream.cern;

public class Polynomial {
	 protected Polynomial() {
     }
 
     public static double p1evl(double x, double coef[], int N) throws ArithmeticException {
         double ans; //˫���ȸ�������p1evl����,����˫����x,��Ӧ��ϵ��coef������N
 //ArithmeticException��������
         ans = x + coef[0];
 
         for (int i = 1; i < N; i++) {
             ans = ans * x + coef[i];
         }
 
         return ans;//����X�Ķ���ʽ��� ����ʽ��ʽx^N+coef[0]*x^(N-1).....
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
