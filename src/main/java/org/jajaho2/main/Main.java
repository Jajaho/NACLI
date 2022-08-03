package org.jajaho2.main;

import org.jajaho2.data.Component;
import org.jajaho2.data.DirectedTypeValuePseudograph;
import org.jajaho2.data.Edge;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        DirectedTypeValuePseudograph<String> graph = new DirectedTypeValuePseudograph<>(Edge.class);

        System.out.println("KnotenPotentialAnalyseCLI");
        System.out.println("Construct the network by connecting nodes like this:");
        System.out.println("             1 R 9 2");
        System.out.println("             ^ ^ ^ ^");
        System.out.println("Source Node _| | | |_ Target Node");
        System.out.println("ComponentType _| |_ Component Value");
        System.out.println("To terminate the program enter: ESC");
        System.out.println("    To validate the graph enter: VAL");
        System.out.println("To calculate the solution enter: CALC");

        Scanner sc = new Scanner(System.in);
        //System.out.println(sc.delimiter());

        // Control Patterns
        Pattern esc = Pattern.compile("(ESC)|(esc)");
        Pattern val = Pattern.compile("(VAL)|(val)");
        Pattern calc = Pattern.compile("(CALC)|(calc)");
        Pattern yes = Pattern.compile("[Yy][Ee][Ss]");
        Pattern no = Pattern.compile("[Nn][Oo]");

        // Input Pattern
        Pattern edge = Pattern.compile("[0-9]\s[IRG]\s[0-9]\s[0-9]");

        while (true) {
            if(sc.findInLine(esc) != null)
                System.exit(1);

            if (sc.findInLine(val) != null) {
                Set<String> acVertices = graph.getAcylclicVertices();
                if (acVertices.isEmpty()) {
                    System.out.println("No floating nodes detected.");
                }
                else {
                    System.out.println("Floating nodes detected: " + acVertices.toString());
                    System.out.println("Do you wish to delete them? (YES/NO)");
                    while (true) {
                        if(sc.findInLine(esc) != null)
                            System.exit(1);

                        if(sc.findInLine(yes) != null) {
                            acVertices.forEach(graph::removeVertex);
                            System.out.println("Floating nodes removed.");
                            break;
                        }
                        if(sc.findInLine(no) != null)
                            break;
                        sc.nextLine();
                    }
                }
            }

            if (sc.findInLine(calc) != null)
                System.exit(3);

            //System.out.println(sc.findInLine(edge));
            inputEdge(sc.findInLine(edge), graph);

            sc.nextLine();
        }
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
        // TODO Directed implementation needed
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
