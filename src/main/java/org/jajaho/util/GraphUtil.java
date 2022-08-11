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
        boolean[] test = new boolean[1];
        int i = 0;

        test[i++] = checkForFloatingVertices(graph, sc);
        test[i++] = checkHasSource(graph);
        // TODO - Catch self loops

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
        getFloatingVertices(graph,leaveSet);
        if (leaveSet.isEmpty()) {
            System.out.println("No floating nodes detected.");
            return true;
        } else {
            System.out.println("Floating nodes detected: " + leaveSet.toString());
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.findInLine(esc) != null)
                    System.exit(1);

                if (sc.findInLine(yes) != null) {
                    leaveSet.forEach(graph::removeVertex);
                    System.out.println("Floating nodes removed.");
                    return true;
                }
                if (sc.findInLine(no) != null) {
                    return false;
                }
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
                getFloatingVertices(graph,leaveSet);
                break;
            }
        }
    }

    private static void removeFloatingVertices(DirectedTypeValuePseudograph<Integer> graph) {
        for (Integer vertex : graph.vertexSet()) {
            if (graph.edgesOf(vertex).size() < 2) {
                graph.removeVertex(vertex);
                removeFloatingVertices(graph);
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
