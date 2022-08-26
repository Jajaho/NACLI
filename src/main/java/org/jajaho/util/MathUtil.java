package org.jajaho.util;

public class MathUtil {


    // TODO - Add gaussian elimination for a.length > 3
    public static double determinant(double[][] a) {
        if (a == null)
            throw new NullPointerException();

        if (a.length != a[0].length)
            throw new IllegalArgumentException("Matrix is not quadratic.");

        int n = a.length;

        switch (a.length) {
            case 1:
                return a[0][0];
            case 2:
                return a[0][0] * a[1][1] - a[1][0] * a[0][1];
            case 3:
                return    a[0][0] * a[1][1] * a[2][2]
                        + a[0][1] * a[1][2] * a[2][0]
                        + a[0][2] * a[1][0] * a[2][1]
                        - a[2][0] * a[1][1] * a[0][2]
                        - a[2][1] * a[1][2] * a[0][0]
                        - a[2][2] * a[1][0] * a[0][1];
            default:
                int d = 0;
                int sign = -1;
                for (int skippedCol = 0; skippedCol < n; ++skippedCol) {
                    double[][] evolvedMatrix = new double[n - 1][n - 1];
                    for (int row = 0; row < n - 1; ++row) {
                        int evolvedMatrixCol = 0;
                        for (int col = 0; col < n; ++col)
                            if (col != skippedCol) evolvedMatrix[row][evolvedMatrixCol++] = a[row + 1][col];
                    }
                    d += (sign *= -1) * a[0][skippedCol] * determinant(evolvedMatrix);
                }
                return d;
        }
    }

    public static boolean isAxisSymmetric(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] - a[j][i] > 1e-10)
                    return false;
            }
        }
        return true;
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
            res[i] = determinant(a_i) / d;
        }
        return res;
    }
}
