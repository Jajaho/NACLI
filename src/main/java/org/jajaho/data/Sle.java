package org.jajaho.data;

import org.jajaho.util.MathUtil;

import java.math.BigDecimal;

public class Sle {

    private BigDecimal[][] a;       // Conductance matrix
    private BigDecimal[] b;         // Current vector

    public Sle(BigDecimal[][] a, BigDecimal[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Dimension mismatch.");
        }
        this.a = a;
        this.b = b;
    }

    public Sle(CircuitGraph graph) {
        int n = graph.vertexSet().size() - 1;
        a = new BigDecimal[n][n];
        for (int i = 0; i < n; i++) {
            a[i] = prefilledWith("0", n);
        }
        b = prefilledWith("0", n);

        for (Integer vertex : graph.vertexSet()) {
            // skip the gnd vertex row
            if (vertex.equals(0))
                continue;
            for (Edge edge : graph.edgesOf(vertex)) {

                switch (graph.getEdgeType(edge)) {
                    case I:
                        Integer targetV = graph.getEdgeTarget(edge);
                        Integer sourceV = graph.getEdgeTarget(edge);
                        // If current source is flowing into the nodal
                        if (targetV.equals(vertex)) {
                            if (!targetV.equals(0))
                                b[targetV - 1] = b[targetV - 1].add(edge.getValue());   // into
                            if (!graph.getEdgeSource(edge).equals(0))
                                b[sourceV - 1] = b[targetV - 1].subtract(edge.getValue());      // out of
                        }
                        break;
                    case R:
                        BigDecimal g = new BigDecimal("1").divide(edge.getValue());
                        addConductanceToMatrix(a, vertex, graph.getOppositeOf(vertex, edge), g);
                        break;
                    case G:
                        addConductanceToMatrix(a, vertex, graph.getOppositeOf(vertex, edge), edge.getValue());
                        break;
                    default:
                        System.out.println("Component not supported.");
                }
            }
        }
    }

    private static void addConductanceToMatrix(BigDecimal[][] a, Integer firstVertex, Integer secondVertex, BigDecimal g) {
        // Offset because the 0th vertex is defined as ground.
        firstVertex -= 1;
        secondVertex -= 1;
        if (firstVertex >= 0) {     // Check for GND nodal
            // Add the first vertex in its own row.
            a[firstVertex][firstVertex] = a[firstVertex][firstVertex].add(g);
            if (secondVertex >= 0) {        // Check for GND nodal
                // Add the vertex on the other end in the row of the first nodal.
                a[firstVertex][secondVertex] = a[firstVertex][secondVertex].subtract(g);
            }
        }
    }

    public void print() {
        if (a == null || b == null) {
            return;
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j].toEngineeringString() + " ");
            }
            System.out.println("| " + b[i]);
        }
    }

    public BigDecimal[] solve() {
        return MathUtil.cramersRule(a, b);
    }

    private BigDecimal[] prefilledWith(String value, int size) {
        BigDecimal[] array = new BigDecimal[size];
        for (int i = 0; i < size; i++) {
            array[i] = new BigDecimal(value);
        }
        return array;
    }

    //---------- Getter & Setter ------------

    public void setA(BigDecimal[][] a) {
        this.a = a;
    }

    public void setB(BigDecimal[] b) {
        this.b = b;
    }

    public BigDecimal[][] getA() {
        return a;
    }

    public BigDecimal[] getB() {
        return b;
    }
}
