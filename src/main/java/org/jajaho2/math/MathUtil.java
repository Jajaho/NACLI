package org.jajaho2.math;

import java.lang.reflect.Array;

public class MathUtil {

    // TODO - Add gaussian elimination for a.length > 3
    public static double determinant(double[][] a) {
        if (a == null)
            throw new NullPointerException();

        if (a.length != a[0].length)
            throw new IllegalArgumentException("Matrix is not quadratic.");

        double determinant = 0.0;
        if (a.length == 2){
            determinant = a[0][0] * a[1][1] - a[1][0] * a[0][1];
        }
        else if (a.length == 3){
            determinant = a[0][0] * a[1][1] * a[2][2]
                        + a[0][1] * a[1][2] * a[2][0]
                        + a[0][2] * a[1][0] * a[2][1]
                        - a[2][0] * a[1][1] * a[0][2]
                        - a[2][1] * a[1][2] * a[0][0]
                        - a[2][2] * a[1][0] * a[0][1];
        }
        return determinant;
    }

    private static double[][] deepCopy(double[][] matrix) {
        return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
    }

    public static double[] cramersRule(double[][] a, double[] b) {
        double[] res = new double[a.length];
        double d = determinant(a);
        for (int i = 0; i < a.length; i++) {
            double[][] a_i = deepCopy(a);
            a_i[i] = b;
            res[i] = determinant(a_i)/d;
        }
        return res;
    }
}
