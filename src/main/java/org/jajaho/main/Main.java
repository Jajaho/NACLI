package org.jajaho.main;

import org.jajaho.data.Component;
import org.jajaho.data.DirectedTypeValuePseudograph;
import org.jajaho.data.Edge;
import org.jajaho.data.Tuple;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    // Global Scanner Object
    static Scanner sc = new Scanner(System.in);

    // Global Control Patterns
    static Pattern esc = Pattern.compile("(ESC)|(esc)");
    static Pattern yes = Pattern.compile("[Yy][Ee][Ss]");
    static Pattern no = Pattern.compile("[Nn][Oo]");

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
                checkForAcyclicVertices(graph);
                checkHasSource(graph);
                continue;
            }

            if (sc.findInLine(calc) != null) {

            }

            //System.out.println(sc.findInLine(edge));
            inputEdge(sc.findInLine(edge), graph);

            sc.nextLine();
        }
    }

    private static void printStartupMsg() {
        System.out.println("Nodal Analysis Command-Line Interface");
        System.out.println("");
        System.out.println("Construct the network by connecting nodes like this:");
        System.out.println("               1 R 9 2");
        System.out.println("               ^ ^ ^ ^");
        System.out.println("Source Nodal  _| | | |_ Target Nodal");
        System.out.println(" Component Type _| |_ Component Value");
        System.out.println("");
        System.out.println("To terminate the program enter: ESC");
        System.out.println("    To validate the graph enter: VAL");
        System.out.println("To calculate the solution enter: CALC");
    }

    private static void checkForAcyclicVertices(DirectedTypeValuePseudograph<Integer> graph) {
        Set<Integer> acVertices = graph.getAcylclicVertices();
        if (acVertices.isEmpty()) {
            System.out.println("No floating nodes detected.");
        } else {
            System.out.println("Floating nodes detected: " + acVertices.toString());
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.findInLine(esc) != null)
                    System.exit(1);

                if (sc.findInLine(yes) != null) {
                    acVertices.forEach(graph::removeVertex);
                    System.out.println("Floating nodes removed.");
                    break;
                }
                if (sc.findInLine(no) != null)
                    break;
                sc.nextLine();
            }
        }
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

    private static boolean checkHasSource(DirectedTypeValuePseudograph<Integer> graph) {
        for (Edge edge : graph.edgeSet()) {
            if (graph.getEdgeType(edge).equals(Component.I) || graph.getEdgeType(edge).equals(Component.U))
                return true;
        }
        System.out.println("Network has no valid supply");
        return false;
    }

    private Tuple<double[][], double[]> makeSLE(DirectedTypeValuePseudograph<Integer> graph) {
        int n = graph.edgeSet().size() - 1;
        double[][] a = new double[n][n];
        double[] b = new double[n];

        for(double[] row : a)
            Arrays.fill(row, 1.0);

        for (Integer vertex : graph.vertexSet()) {
            for (Edge edge : graph.edgesOf(vertex)) {
                switch (graph.getEdgeType(edge)) {
                    case I:
                        b[graph.getEdgeTarget(edge)] += graph.getEdgeWeight(edge);
                        b[graph.getEdgeSource(edge)] -= graph.getEdgeWeight(edge);
                        break;
                    case R:

                        break;
                    case G:

                        break;
                    default:
                        System.out.println("EdgeType not supported.");
                        break;
                }
            }
        }

        return null;
    }

    private static void inputEdge(String input, DirectedTypeValuePseudograph<Integer> graph) {
        if (input == null) {
            System.out.println("No valid edge detected.");
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
