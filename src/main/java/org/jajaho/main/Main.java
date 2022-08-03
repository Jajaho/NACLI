package org.jajaho.main;

import org.jajaho.data.Component;
import org.jajaho.data.DirectedTypeValuePseudograph;
import org.jajaho.data.Edge;
import org.jajaho.data.Tuple;

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
        DirectedTypeValuePseudograph<String> graph = new DirectedTypeValuePseudograph<>(Edge.class);

        // Local Control Patterns
        Pattern val = Pattern.compile("(VAL)|(val)");
        Pattern calc = Pattern.compile("(CALC)|(calc)");

        // Input Pattern
        Pattern edge = Pattern.compile("[0-9]\s[IRG]\s[0-9]\s[0-9]");

        printWelcomeMsg();

        while (true) {
            if (sc.findInLine(esc) != null)
                System.exit(1);

            if (sc.findInLine(val) != null) {
                checkForAcyclicVertices(graph);
                continue;
            }

            if (sc.findInLine(calc) != null) {
                String gnd = defGndNode(graph);
            }

            //System.out.println(sc.findInLine(edge));
            inputEdge(sc.findInLine(edge), graph);

            sc.nextLine();
        }
    }

    private static void printWelcomeMsg() {
        System.out.println("Nodal Analysis Command-Line Interface");
        System.out.println("");
        System.out.println("Construct the network by connecting nodes like this:");
        System.out.println("             1 R 9 2");
        System.out.println("             ^ ^ ^ ^");
        System.out.println("Source Node _| | | |_ Target Node");
        System.out.println("ComponentType _| |_ Component Value");
        System.out.println("");
        System.out.println("To terminate the program enter: ESC");
        System.out.println("    To validate the graph enter: VAL");
        System.out.println("To calculate the solution enter: CALC");
    }

    private static void checkForAcyclicVertices(DirectedTypeValuePseudograph<String> graph) {
        Set<String> acVertices = graph.getAcylclicVertices();
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

    private static String defGndNode(DirectedTypeValuePseudograph<String> graph) {
        String gnd;
        System.out.println("Define GND Nodal.");

        while (true) {
            String res = sc.findInLine(Pattern.compile("[0-9]"));
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

    private Tuple<double[][], double[]> makeSLE(DirectedTypeValuePseudograph<String> graph, String gnd) {
        double[][] a;
        double[] b;

        for (String vertex: graph.vertexSet()) {
            for (Edge edge: graph.edgesOf(vertex)) {

            }
        }

        return null;
    }

    private static void inputEdge(String input, DirectedTypeValuePseudograph<String> graph) {
        if (input == null) {
            System.out.println("No valid edge detected.");
            return;
        }

        int sourceDel = input.indexOf("\s");
        int typeDel = input.indexOf("\s", sourceDel + 1);
        int valueDel = input.indexOf("\s", typeDel + 1);

        String source = input.substring(0, sourceDel);
        Component type = Component.valueOf(input.substring(sourceDel + 1, typeDel));
        double value = Double.parseDouble(input.substring(typeDel + 1, valueDel));
        String target = input.substring(valueDel + 1);

        graph.addVertex(source);
        graph.addVertex(target);

        Edge e = (Edge) graph.addEdge(source, target);
        // maybe use Supplier<E> as edgeSupplier
        graph.setEdgeType(e, type);
        graph.setEdgeWeight(e, value);

        System.out.println("New connection made.");
    }

    private static void testGraph(DirectedTypeValuePseudograph<String> graph) {
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        Edge e0 = (Edge) graph.addEdge("0", "1");
        e0.setComponentType(Component.I);
        Edge e1 = (Edge) graph.addEdge("1", "2");
        Edge e2 = (Edge) graph.addEdge("2", "0");

        graph.setEdgeWeight(e0, 5.6);
        graph.setEdgeWeight(e2, -5.6);

        System.out.println(graph);
    }
}
