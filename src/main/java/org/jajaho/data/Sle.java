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

    public Sle(CircuitGraph graph) {
        int n = graph.vertexSet().size() - 1;
        a = new double[n][n];
        b = new double[n];

        for (Integer vertex : graph.vertexSet()) {
            // skip the gnd vertex row
            if (vertex.equals(0))
                continue;
            for (Edge edge : graph.edgesOf(vertex)) {

                switch (graph.getEdgeType(edge)) {
                    case I:
                        if (graph.getEdgeTarget(edge).equals(vertex)) {
                            if (!graph.getEdgeTarget(edge).equals(0))
                                b[graph.getEdgeTarget(edge) - 1] += graph.getEdgeWeight(edge);
                            if (!graph.getEdgeSource(edge).equals(0))
                                b[graph.getEdgeSource(edge) - 1] -= graph.getEdgeWeight(edge);
                        }
                        break;
                    case R: {
                        double g = 1 / graph.getEdgeWeight(edge);
                        addConductanceToMatrix(a, vertex, graph.getOppositeOf(vertex, edge), g);
                    }
                    break;
                    case G: {
                        double g = graph.getEdgeWeight(edge);
                        addConductanceToMatrix(a, vertex, graph.getOppositeOf(vertex, edge), g);
                    }
                    break;
                    default:
                        System.out.println("Component not supported.");
                        break;
                }
            }
        }
    }

    private static void addConductanceToMatrix(double[][] a, Integer firstVertex, Integer secondVertex, double g) {
        // Offset because the 0th vertex is defined as ground.
        firstVertex -= 1;
        secondVertex -= 1;
        if (firstVertex >= 0) {     // Check for GND nodal
            // Add the first vertex in its own row.
            a[firstVertex][firstVertex] += g;
            if (secondVertex >= 0) {        // Check for GND nodal
                // Add the vertex on the other end in the row of the first nodal.
                a[firstVertex][secondVertex] -= g;
            }
        }
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
