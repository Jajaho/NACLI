package org.jajaho.util;

import org.jajaho.data.Component;
import org.jajaho.data.DirectedTypeValuePseudograph;
import org.jajaho.data.Edge;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class GraphUtil {
    public static boolean validateGraph(DirectedTypeValuePseudograph<Integer> graph, Scanner sc) {
        boolean[] test = new boolean[3];        // Important: Increse array length when adding a new test!
        int i = 0;

        // TODO - Check whether graph is empty
        test[i++] = checkForSelfLoops(graph,sc);
        test[i++] = checkForFloatingVertices(graph, sc);
        test[i++] = checkHasSource(graph);

        for (int j = 0; j < test.length; j++) {
            if (!test[j]) {
                System.out.println("Validation failed.");
                return false;
            }
        }
        System.out.println("All tests successful.");
        return true;
    }

    private static boolean checkForFloatingVertices(DirectedTypeValuePseudograph<Integer> graph, Scanner sc) {
        Pattern esc = Pattern.compile("(ESC)|(esc)");
        Pattern yes = Pattern.compile("[Yy][Ee][Ss]");
        Pattern no = Pattern.compile("[Nn][Oo]");

        Set<Integer> leaveSet = new HashSet<>();
        getFloatingVertices(graph, leaveSet);

        if (leaveSet.isEmpty()) {
            System.out.println("No floating nodes detected.");
            return true;
        } else {
            System.out.println("Floating nodals detected: " + leaveSet.toString());
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.findInLine(esc) != null) {
                    System.exit(1);
                }
                if (sc.findInLine(yes) != null) {
                    leaveSet.forEach(graph::removeVertex);
                    System.out.println("Floating nodes removed.");
                    return true;
                }
                if (sc.findInLine(no) != null) {
                    return false;
                }
                sc.nextLine();
            }
        }
    }

    private static void getFloatingVertices(DirectedTypeValuePseudograph<Integer> graph, Set<Integer> leaveSet) {
        for (Integer vertex : graph.vertexSet()) {
            if (leaveSet.contains(vertex))
                continue;

            Set<Edge> edges = graph.edgesOf(vertex);
            int edgeCount = edges.size();

            for (Edge e : edges) {
                if (leaveSet.contains(graph.getOppositeOf(vertex, e)))
                    edgeCount--;
            }

            if (edgeCount < 2) {
                leaveSet.add(vertex);
                getFloatingVertices(graph, leaveSet);
                break;
            }
        }
    }

    private static boolean checkForSelfLoops(DirectedTypeValuePseudograph<Integer> graph, Scanner sc) {
        Pattern esc = Pattern.compile("(ESC)|(esc)");
        Pattern yes = Pattern.compile("[Yy][Ee][Ss]");
        Pattern no = Pattern.compile("[Nn][Oo]");

        Set<Edge> sLSet = new HashSet<>();

        for (Edge e : graph.edgeSet()) {
            if (graph.getEdgeTarget(e).equals(graph.getEdgeSource(e))) {
                sLSet.add(e);
            }
        }

        if (sLSet.isEmpty()) {
            System.out.println("No self-loops detected.");
            return true;
        } else {
            System.out.println("Self-loops detected: " + sLSet.toString());
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.findInLine(esc) != null) {
                    System.exit(1);
                }
                if (sc.findInLine(yes) != null) {
                    sLSet.forEach(graph::removeEdge);
                    System.out.println("Self-loops removed.");
                    return true;
                }
                if (sc.findInLine(no) != null) {
                    return false;
                }
                sc.nextLine();
            }
        }
    }

    private static boolean checkHasSource(DirectedTypeValuePseudograph<Integer> graph) {
        for (Edge edge : graph.edgeSet()) {
            if (graph.getEdgeType(edge).equals(Component.I) || graph.getEdgeType(edge).equals(Component.U))
                System.out.println("Network has a valid source.");
            return true;
        }
        System.out.println("Network has no valid supply.");
        return false;
    }
}
