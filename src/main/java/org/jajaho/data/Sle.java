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
                System.out.print(a[i][j] + " ");
            }
            System.out.println("| " + b[i]);
        }
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
