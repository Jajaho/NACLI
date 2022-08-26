package org.jajaho.main;

import org.jajaho.data.*;
import org.jajaho.util.GraphUtil;
import org.jajaho.util.MathUtil;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    // Global Scanner Object
    static Scanner sc = new Scanner(System.in);

    // Global Control Patterns
    static Pattern esc = Pattern.compile("(ESC)|(esc)");

    public static void main(String[] args) {
        DirectedTypeValuePseudograph<Integer> graph = new DirectedTypeValuePseudograph<>(Edge.class);

        // Local Control Patterns
        Pattern val = Pattern.compile("(VAL)|(val)");
        Pattern calc = Pattern.compile("(CALC)|(calc)");

        // Input Pattern
        Pattern edge = Pattern.compile("[0-9]\s[IRG]\s[0-9]\s[0-9]");

        printStartupMsg();

        while (true) {
            if (sc.findInLine(esc) != null)
                System.exit(1);

            if (sc.findInLine(val) != null) {
                GraphUtil.validateGraph(graph, sc);
            }

            if (sc.findInLine(calc) != null) {
                if (!GraphUtil.validateGraph(graph, sc)) {
                    System.out.println("Network invalid - calculation aborted.");
                    continue;
                }
                Sle sle = makeSLE(graph);
                sle.print();

                // Post conversion validation
                if (!MathUtil.isAxisSymmetric(sle.a)) {
                    System.out.println("Matrix is not symmetric - calculation aborted.");
                    continue;
                }
                double[] phis = MathUtil.cramersRule(sle.a, sle.b);
                System.out.println("Voltages at nodals (referenced to nodal 0):");
                for (int i = 1; i - 1 < phis.length; i++) {
                    System.out.println("V" + i + "= " + phis[i - 1]);
                }
            }
            inputEdge(sc.findInLine(edge), graph);
            sc.nextLine();
        }
    }

    private static void printStartupMsg() {
        System.out.println("| \\ ||   /_\\   | __| ||    ||");
        System.out.println("||\\\\||  //_\\\\  ||__  ||__  ||");
        System.out.println("|| \\ | //   \\\\ |___| |___| || byJakob");
        System.out.println("-------------------------------------");
        System.out.println("Nodal Analysis Command Line Interface");
        System.out.println("");
        System.out.println("Construct the network by connecting nodals like this:");
        System.out.println("                      1 R 9 2");
        System.out.println("                      ^ ^ ^ ^");
        System.out.println("       Source Nodal  _| | | |_ Target Nodal");
        System.out.println("        Component Type _| |_ Component Value");
        System.out.println("");
        System.out.println("To terminate the program enter: ESC");
        System.out.println("    To validate the graph enter: VAL");
        System.out.println("To calculate the solution enter: CALC");
    }



    private static Sle makeSLE(DirectedTypeValuePseudograph<Integer> graph) {
        int n = graph.vertexSet().size() - 1;
        double[][] a = new double[n][n];
        double[] b = new double[n];

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
        return new Sle(a, b);
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

    private static void inputEdge(String input, DirectedTypeValuePseudograph<Integer> graph) {
        if (input == null) {
            //System.out.println("No valid edge detected.");
            return;
        }

        int sourceDel = input.indexOf("\s");
        int typeDel = input.indexOf("\s", sourceDel + 1);
        int valueDel = input.indexOf("\s", typeDel + 1);

        Integer source = Integer.parseInt(input.substring(0, sourceDel));
        Integer target = Integer.parseInt(input.substring(valueDel + 1));
        Component type = Component.valueOf(input.substring(sourceDel + 1, typeDel));
        double value = Double.parseDouble(input.substring(typeDel + 1, valueDel));

        graph.addVertex(source);
        graph.addVertex(target);

        Edge e = graph.addEdge(source, target);
        // maybe use Supplier<E> as edgeSupplier
        graph.setEdgeType(e, type);
        graph.setEdgeWeight(e, value);

        System.out.println("New connection made.");
    }

    // returns null if the pattern couldn't be matched
    private static Integer parseUserInput(Pattern p) {
        Integer res;
        try {
            res = Integer.parseInt(sc.findInLine(p));
        } catch (NumberFormatException e) {
            res = null;
        }
        return res;
    }

    private static Integer defGndNode(DirectedTypeValuePseudograph<Integer> graph) {
        Integer gnd;
        System.out.println("Define GND Nodal.");

        while (true) {
            Integer res = parseUserInput(Pattern.compile("[0-9]"));
            if (res == null) {
                sc.nextLine();
                continue;
            }

            if (graph.containsVertex(res)) {
                gnd = res;
                break;
            } else {
                System.out.println("Nodal not found.");
            }
            sc.nextLine();
        }
        return gnd;
    }

    private static void testGraph(DirectedTypeValuePseudograph<Integer> graph) {
        graph.addVertex(0);
        graph.addVertex(1);
        graph.addVertex(2);

        Edge e0 = graph.addEdge(0, 1);
        e0.setComponentType(Component.I);
        Edge e1 = graph.addEdge(1, 2);
        Edge e2 = graph.addEdge(2, 0);

        graph.setEdgeWeight(e0, 5.6);
        graph.setEdgeWeight(e2, -5.6);

        System.out.println(graph);
    }
}
