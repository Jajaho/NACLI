package org.jajaho.data;

public class Tuple<A, B> {

    A a;
    B b;

    public Tuple(){}

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
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
