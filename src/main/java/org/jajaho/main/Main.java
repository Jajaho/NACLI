package org.jajaho.main;

import org.jajaho.data.Component;
import org.jajaho.data.CircuitGraph;
import org.jajaho.data.Edge;
import org.jajaho.data.Sle;
import org.jajaho.util.GraphUtil;
import org.jajaho.util.MathUtil;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    // Global Terminal Scanner
    static Scanner tScan = new Scanner(System.in);
    // Global Escape Command Pattern
    static Pattern esc = Pattern.compile("esc", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        CircuitGraph graph = new CircuitGraph(Edge.class);

        // Level 1 Command Patterns
        Pattern validate = Pattern.compile("val", Pattern.CASE_INSENSITIVE);        // Validate the graph
        Pattern calculate = Pattern.compile("calc", Pattern.CASE_INSENSITIVE);      // Calculate the result
        Pattern add = Pattern.compile("add", Pattern.CASE_INSENSITIVE);             // Add edge
        Pattern remove = Pattern.compile("rem", Pattern.CASE_INSENSITIVE);          // Remove edge
        Pattern show = Pattern.compile("show", Pattern.CASE_INSENSITIVE);           // Show all edges between vertices
        Pattern read = Pattern.compile("read", Pattern.CASE_INSENSITIVE);           // Read file

        // Input Pattern Strings
        String intPatStr = "0|([1-9][0-9]*)";
        String namePatStr = "[IRG]" + intPatStr;
        String doublePatStr = "^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$";

        printStartupMsg();

        while (true) {
            if (tScan.hasNext(esc)) {       // Escape
                tScan.next(esc);
                System.exit(1);
            }

            if (tScan.hasNext(validate)) {      // Validate
                tScan.next(validate);
                GraphUtil.validateGraph(graph, tScan);
            }

            if (tScan.hasNext(calculate)) {         // Calculate
                tScan.next(calculate);
                if (!GraphUtil.validateGraph(graph, tScan)) {
                    System.out.println("Network invalid - calculation aborted.");
                    tScan.nextLine();
                    continue;
                }
                Sle sle = new Sle(graph);
                sle.print();

                // Post conversion validation
                if (!MathUtil.isAxisSymmetric(sle.a)) {
                    System.out.println("Matrix is not symmetric - calculation aborted.");
                    tScan.nextLine();
                    continue;
                }
                double[] phis = MathUtil.cramersRule(sle.a, sle.b);
                System.out.println("Voltages at nodals (referenced to nodal 0):");
                for (int i = 1; i - 1 < phis.length; i++) {
                    System.out.println("V" + i + "= " + phis[i - 1]);
                }
            }

            if (tScan.hasNext(add)) {       // Add edge
                tScan.next(add);

                int source, target;
                Component type;
                String name;
                BigDecimal val;

                try {
                    name = tScan.next(namePatStr);
                } catch (Exception e) {
                    System.out.println("Invalid source vertex");
                    tScan.nextLine();
                    continue;
                }

                try {
                    source = Integer.parseInt(tScan.next(intPatStr));
                } catch (Exception e) {
                    System.out.println("Invalid source vertex");
                    tScan.nextLine();
                    continue;
                }

                try {
                    type = Component.valueOf(tScan.next(component));
                } catch (Exception e) {
                    System.out.println("Invalid component type.");
                    tScan.nextLine();
                    continue;
                }

                try {
                    val = tScan.nextDouble();
                } catch (Exception e) {
                    System.out.println("Invalid component value.");
                    System.out.println("Note: Depending on localisation, component values have to be typed with either , or .");
                    tScan.nextLine();
                    continue;
                }

                try {
                    target = Integer.parseInt(tScan.next(intPatStr));
                } catch (Exception e) {
                    System.out.println("Invalid target vertex");
                    tScan.nextLine();
                    continue;
                }

                graph.addVertex(source);
                graph.addVertex(target);
                Edge e = graph.addEdge(source,target);
                graph.setEdgeType(e, type);
                graph.setEdgeWeight(e, val);
                System.out.println("New connection made.");
            }
            tScan.nextLine();
        }
    }

    private static void printStartupMsg() {
        System.out.println("| \\ ||   /_\\   | __| ||    ||");
        System.out.println("||\\\\||  //_\\\\  ||__  ||__  ||");
        System.out.println("|| \\ | //   \\\\ |___| |___| || byJakob");
        System.out.println("-------------------------------------");
        System.out.println("Nodal Analysis Command Line Interface");
        System.out.println();
        System.out.println("Construct the network by connecting nodals like this:");
        System.out.println("                  add R1 0 1 470");
        System.out.println("                      ^  ^ ^ ^");
        System.out.println("       Source Nodal  _|  | | |_ Target Nodal");
        System.out.println("         Component Type _| |_ Component Value");
        System.out.println();
        System.out.println("Type:");
        System.out.println("esc - exit the program");
        System.out.println("val - validate the graph");
        System.out.println("calc - calculate the solution");
    }



    // returns null if the pattern couldn't be matched
    private static Integer parseUserInput(Pattern p) {
        Integer res;
        try {
            res = Integer.parseInt(tScan.findInLine(p));
        } catch (NumberFormatException e) {
            res = null;
        }
        return res;
    }

    private static Integer defGndNode(CircuitGraph graph) {
        int gnd;
        System.out.println("Define GND Nodal.");

        while (true) {
            Integer res = parseUserInput(Pattern.compile("[0-9]"));
            if (res == null) {
                tScan.nextLine();
                continue;
            }

            if (graph.containsVertex(res)) {
                gnd = res;
                break;
            } else {
                System.out.println("Nodal not found.");
            }
            tScan.nextLine();
        }
        return gnd;
    }
}
