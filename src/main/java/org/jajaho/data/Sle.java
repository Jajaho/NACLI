package org.jajaho.data;

public class Sle {

    public double[][] a;
    public double[] b;

    public Sle(){}

    public Sle(double[][] a,double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Dimension mismatch.");
        }
        this.a = a;
        this.b = b;
    }

    public void print() {
        if (a == null || b == null) {
            return;
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(toFormatDouble(a[i][j]) + " ");
            }
            System.out.println("| " + b[i]);
        }
    }

    private static String toFormatDouble(double n) {
        double absN = Math.abs(n);
        if (absN > 10e6 || absN < 10e-5) {
            return String.format("%1.2e", n);
        }
        else if (absN < 1) {
            return String.format("%1.4", n);
        }
        return String.format("%7", n);
    }
    /*
    public void setValue1(A a) {
        this.a = a;
    }

    public void setValue2(B b) {
        this.b = b;
    }

    public A getValue1() {
        return a;
    }

    public A getValue1() {
        return a;
    }

     */
}
