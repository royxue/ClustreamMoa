package Clustream.addon;

import java.util.ArrayList;

public class Miniball {

    int d;
    ArrayList L;
    Miniball_b B;
    int support_end = 0;

    public Miniball(int dim) {
        d = dim;
        L = new ArrayList();
        B = new Miniball_b();
    }

    class pvt {
        int val;

        pvt() {
            val = 0;
        }

        void setVal(int i) {
            val = i;
        }

        int getVal() {
            return (val);
        }
    }

    class Miniball_b {

        int m, s;   // size and number of support points
        double[] q0 = new double[d];
        double[] z = new double[d + 1];
        double[] f = new double[d + 1];
        double[][] v = new double[d + 1][d];
        double[][] a = new double[d + 1][d];
        double[][] c = new double[d + 1][d];
        double[] sqr_r = new double[d + 1];
        double[] current_c = new double[d]; // refers to some c[j]
        double current_sqr_r;


        double[] getCenter() {
            return (current_c);
        }

        double squared_radius() {
            return current_sqr_r;
        }

        int size() {
            return m;
        }

        int support_size() {
            return s;
        }

        double excess(double[] p) {
            double e = -current_sqr_r;
            for (int k = 0; k < d; ++k) {
                e += mb_sqr(p[k] - current_c[k]);
            }
            return e;
        }

        void reset() {
            m = 0;
            s = 0;
            // we misuse c[0] for the center of the empty sphere
            for (int j = 0; j < d; j++) {
                c[0][j] = 0;
            }
            current_c = c[0];
            current_sqr_r = -1;
        }

        void pop() {
            --m;
        }

        boolean push(double[] p) {
            //System.out.println("Miniball_b:push");
            int i, j;
            double eps = 1e-32;

            if (m == 0) {
                for (i = 0; i < d; ++i) {
                    q0[i] = p[i];
                }
                for (i = 0; i < d; ++i) {
                    c[0][i] = q0[i];
                }
                sqr_r[0] = 0;
            } else {
                // set v_m to Q_m
                for (i = 0; i < d; ++i) {
                    v[m][i] = p[i] - q0[i];
                }

                // compute the a_{m,i}, i< m
                for (i = 1; i < m; ++i) {
                    a[m][i] = 0;
                    for (j = 0; j < d; ++j) {
                        a[m][i] += v[i][j] * v[m][j];
                    }
                    a[m][i] *= (2 / z[i]);
                }

                // update v_m to Q_m-\bar{Q}_m
                for (i = 1; i < m; ++i) {
                    for (j = 0; j < d; ++j) {
                        v[m][j] -= a[m][i] * v[i][j];
                    }
                }

                // compute z_m
                z[m] = 0;
                for (j = 0; j < d; ++j) {
                    z[m] += mb_sqr(v[m][j]);
                }
                z[m] *= 2;

                // reject push if z_m too small
                if (z[m] < eps * current_sqr_r) {
                    return false;
                }

                // update c, sqr_r
                double e = -sqr_r[m - 1];
                for (i = 0; i < d; ++i) {
                    e += mb_sqr(p[i] - c[m - 1][i]);
                }
                f[m] = e / z[m];

                for (i = 0; i < d; ++i) {
                    c[m][i] = c[m - 1][i] + f[m] * v[m][i];
                }
                sqr_r[m] = sqr_r[m - 1] + e * f[m] / 2;
            }
            current_c = c[m];
            current_sqr_r = sqr_r[m];
            s = ++m;
            return true;
        }

        double slack() {
            double min_l = 0;
            double[] l = new double[d + 1];
            l[0] = 1;
            for (int i = s - 1; i > 0; --i) {
                l[i] = f[i];
                for (int k = s - 1; k > i; --k) {
                    l[i] -= a[k][i] * l[k];
                }
                if (l[i] < min_l) {
                    min_l = l[i];
                }
                l[0] -= l[i];
            }
            if (l[0] < min_l) {
                min_l = l[0];
            }
            return ((min_l < 0) ? -min_l : 0);
        }
    }

    public void clear() {
        L.clear();
    }

    public void check_in(double[] p) {
        if (p != null) {
            L.add(p);
        } else {
            System.out.println("Miniball.check_in WARNING: Skipping null point");
        }
    }


    public void build() {
        B.reset();
        support_end = 0;
        pivot_mb(points_end());
    }

    void mtf_mb(int i) {
        int pj = 0;
        support_end = points_begin();
        if ((B.size()) == d + 1) {
            return;
        }
        for (int k = points_begin(); k != i;) {
            pj = pj + 1;
            int j = k++;
            double[] sp = (double[]) L.get(j);
            if (B.excess(sp) > 0) {
                if (B.push(sp)) {
                    mtf_mb(j);
                    B.pop();
                    move_to_front(j);
                }
            }
        }
    }

    void move_to_front(int j) {

        if (support_end <= j) {
            support_end++;
        }
        //   L.splice (L.begin(), L, j);
        double[] sp = (double[]) L.get(j);
        L.remove(j);
        L.add(0, sp);
    }

    void pivot_mb(int i) {
        int t = 1;
        mtf_mb(t);
        double max_e = 0.0, old_sqr_r = -1;
        pvt pivot = new pvt();
        do {
            max_e = max_excess(t, i, pivot);
            if (max_e > 0) {
                t = support_end;
                if (t == pivot.getVal()) {
                    ++t;
                }
                old_sqr_r = B.squared_radius();
                double[] sp = (double[]) L.get(pivot.getVal());
                B.push(sp);
                mtf_mb(support_end);
                B.pop();
                move_to_front(pivot.getVal());
            }
        } while ((max_e > 0) && (B.squared_radius() > old_sqr_r));
    }

    double max_excess(int t, int i, pvt pivot) {
        double[] c = B.getCenter();
        double sqr_r = B.squared_radius();
        double e, max_e = 0;
        for (int k = t; k != i; ++k) {
            double[] p = (double[]) L.get(k);

            e = -sqr_r;
            for (int j = 0; j < d; ++j) {
                e += mb_sqr(p[j] - c[j]);
            }
            if (e > max_e) {
                max_e = e;
                pivot.setVal(k);
            }
        }
        return max_e;
    }

    public double[] center() {
        return B.getCenter();
    }

    public double squared_radius() {
        return B.squared_radius();
    }

    public double radius() {
        return ((1 + 0.00001) * Math.sqrt(B.squared_radius()));
    }

    public int nr_points() {
        return L.size();
    }

    int points_begin() {
        return (0);
    }

    int points_end() {
        return (L.size());
    }

    public int nr_support_points() {
        return B.support_size();
    }

    int support_points_begin() {
        return (0);
    }

    int support_points_end() {
        return support_end;
    }

    double accuracy(double slack) {
        double e, max_e = 0;
        int n_supp = 0;

        int i;
        for (i = points_begin(); i != support_end; ++i, ++n_supp) {
            double[] sp = (double[]) L.get(i);
            if ((e = Math.abs(B.excess(sp))) > max_e) {
                max_e = e;
            }
        }
        if (n_supp == nr_support_points()) {
            System.out.println("Miniball.accuracy WARNING: STRANGE PROBLEM HERE!");
        }
        for (i = support_end; i != points_end(); ++i) {
            double[] sp = (double[]) L.get(i);
            if ((e = B.excess(sp)) > max_e) {
                max_e = e;
            }
        }
        slack = B.slack();
        return (max_e / squared_radius());
    }

    boolean is_valid(double tolerance) {
        double slack = 0.0;
        return ((accuracy(slack) < tolerance) && (slack == 0));
    }

    double mb_sqr(double r) {
        return r * r;
    }
}
