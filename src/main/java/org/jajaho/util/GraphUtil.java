package org.jajaho.util;

import org.jajaho.data.CircuitGraph;
import org.jajaho.data.Component;
import org.jajaho.data.Edge;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.jajaho.data.GlobalPattens.*;

public class GraphUtil {
    public static boolean validateGraph(CircuitGraph graph, Scanner sc) {
        boolean[] test = new boolean[3];        // Important: Increase array length when adding a new test!
        int i = 0;

        // TODO - Check whether graph is empty
        test[i++] = checkForSelfLoops(graph, sc);
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

    public static boolean isUniqueName(CircuitGraph graph, String name) {
        for (Edge e : graph.edgeSet()) {
            if (name.equals(e.getName()))
                return false;
        }
        return true;
    }

    private static boolean checkForFloatingVertices(CircuitGraph graph, Scanner sc) {
        Set<Integer> leaveSet = new HashSet<>();
        getFloatingVertices(graph, leaveSet);

        if (leaveSet.isEmpty()) {
            System.out.println("No floating nodals detected.");
            return true;
        } else {
            System.out.println("Floating nodals detected: " + leaveSet);
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.hasNext(esc)) {
                    System.exit(1);
                }
                if (sc.hasNext(yes)) {
                    sc.next(yes);
                    leaveSet.forEach(graph::removeVertex);
                    System.out.println("Floating nodes removed.");
                    return true;
                }
                if (sc.hasNext(no)) {
                    sc.hasNext(no);
                    return false;
                }
                sc.nextLine();
            }
        }
    }

    private static void getFloatingVertices(CircuitGraph graph, Set<Integer> leaveSet) {
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

    private static boolean checkForSelfLoops(CircuitGraph graph, Scanner sc) {
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
            System.out.println("Self-loops detected: " + sLSet);
            System.out.println("Do you wish to delete them? (YES/NO)");
            while (true) {
                if (sc.hasNext(esc)) {
                    System.exit(1);
                }
                if (sc.hasNext(yes)) {
                    sc.next(yes);
                    sLSet.forEach(graph::removeEdge);
                    System.out.println("Self-loops removed.");
                    return true;
                }
                if (sc.hasNext(no)) {
                    sc.hasNext(no);
                    return false;
                }
                sc.nextLine();
            }
        }
    }

    private static boolean checkHasSource(CircuitGraph graph) {
        for (Edge edge : graph.edgeSet()) {
            if (edge.getComponentType().equals(Component.I) || edge.getComponentType().equals(Component.U))
                System.out.println("Network has a valid source.");
            return true;
        }
        System.out.println("Network has no valid supply.");
        return false;
    }
}
