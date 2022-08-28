// Code from Seyyed Mohsen Mousavi on stackoverflow: https://stackoverflow.com/users/8555515/seyyed-mohsen-mousavi
package org.jajaho.util;

import java.math.BigDecimal;
import java.util.Arrays;


public class DeterminantUtil {

    private double[][] matrix;
    private int sign = 1;


    DeterminantUtil(double[][] matrix) {
        this.matrix = matrix;
    }

    DeterminantUtil(BigDecimal[][] matrix) {
        this.matrix = toDoubleMatrix(matrix);
    }

    private static double[][] toDoubleMatrix(BigDecimal[][] matrix) {
        int n = matrix.length;
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = matrix[i][j].doubleValue();
            }
        }
        return res;
    }

    public int getSign() {
        return sign;
    }

    public BigDecimal determinant() {

        BigDecimal deter;
        if (isUpperTriangular() || isLowerTriangular())
            deter = multiplyDiameter().multiply(BigDecimal.valueOf(sign));

        else {
            makeTriangular();
            deter = multiplyDiameter().multiply(BigDecimal.valueOf(sign));

        }
        return deter;
    }


    /*  receives a matrix and makes it triangular using allowed operations
        on columns and rows
    */
    public void makeTriangular() {

        for (int j = 0; j < matrix.length; j++) {
            sortCol(j);
            for (int i = matrix.length - 1; i > j; i--) {
                if (matrix[i][j] == 0)
                    continue;

                double x = matrix[i][j];
                double y = matrix[i - 1][j];
                multiplyRow(i, (-y / x));
                addRow(i, i - 1);
                multiplyRow(i, (-x / y));
            }
        }
    }


    public boolean isUpperTriangular() {

        if (matrix.length < 2)
            return false;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < i; j++) {
                if (matrix[i][j] != 0)
                    return false;

            }

        }
        return true;
    }


    public boolean isLowerTriangular() {

        if (matrix.length < 2)
            return false;

        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; j > i; i++) {
                if (matrix[i][j] != 0)
                    return false;

            }

        }
        return true;
    }


    public BigDecimal multiplyDiameter() {

        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == j)
                    result = result.multiply(BigDecimal.valueOf(matrix[i][j]));

            }

        }
        return result;
    }


    // when matrix[i][j] = 0 it makes it's value non-zero
    public void makeNonZero(int rowPos, int colPos) {

        int len = matrix.length;

        outer:
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (matrix[i][j] != 0) {
                    if (i == rowPos) { // found "!= 0" in its own row, so cols must be added
                        addCol(colPos, j);
                        break outer;

                    }
                    if (j == colPos) { // found "!= 0" in its own col, so rows must be added
                        addRow(rowPos, i);
                        break outer;
                    }
                }
            }
        }
    }


    //add row1 to row2 and store in row1
    public void addRow(int row1, int row2) {

        for (int j = 0; j < matrix.length; j++)
            matrix[row1][j] += matrix[row2][j];
    }


    //add col1 to col2 and store in col1
    public void addCol(int col1, int col2) {

        for (int i = 0; i < matrix.length; i++)
            matrix[i][col1] += matrix[i][col2];
    }


    //multiply the whole row by num
    public void multiplyRow(int row, double num) {

        if (num < 0)
            sign *= -1;


        for (int j = 0; j < matrix.length; j++) {
            matrix[row][j] *= num;
        }
    }


    //multiply the whole column by num
    public void multiplyCol(int col, double num) {

        if (num < 0)
            sign *= -1;

        for (int i = 0; i < matrix.length; i++)
            matrix[i][col] *= num;

    }


    // sort the cols from the biggest to the lowest value
    public void sortCol(int col) {

        for (int i = matrix.length - 1; i >= col; i--) {
            for (int k = matrix.length - 1; k >= col; k--) {
                double tmp1 = matrix[i][col];
                double tmp2 = matrix[k][col];

                if (Math.abs(tmp1) < Math.abs(tmp2))
                    replaceRow(i, k);
            }
        }
    }


    //replace row1 with row2
    public void replaceRow(int row1, int row2) {

        if (row1 != row2)
            sign *= -1;

        double[] tempRow = new double[matrix.length];

        for (int j = 0; j < matrix.length; j++) {
            tempRow[j] = matrix[row1][j];
            matrix[row1][j] = matrix[row2][j];
            matrix[row2][j] = tempRow[j];
        }
    }
}
