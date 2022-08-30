package org.jajaho.main;

import org.jajaho.data.*;
import org.jajaho.util.GraphUtil;
import org.jajaho.util.MathUtil;
import org.jajaho.util.ReadUtil;
import picocli.CommandLine;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import static org.jajaho.data.GlobalPattens.*;

@CommandLine.Command(
        name = "nacli",
        description = "Says hello",
        version = "nacli 0.1.0",
        mixinStandardHelpOptions = true
)

public class Main implements Callable<Integer> {

    // Global Terminal Scanner
    static Scanner tScan = new Scanner(System.in);

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public void mainInterface() {
        CircuitGraph graph = new CircuitGraph(Edge.class);

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
                if (!MathUtil.isAxisSymmetric(sle.getA())) {
                    System.out.println("Matrix is not symmetric - calculation aborted.");
                    tScan.nextLine();
                    continue;
                }
                BigDecimal[] phis = sle.solve();
                System.out.println("Voltages at nodals (referenced to nodal 0):");
                for (int i = 1; i - 1 < phis.length; i++) {
                    System.out.println("V" + i + "= " + phis[i - 1].toEngineeringString() + " V");
                }
            }

            if (tScan.hasNext(add)) {       // Add edge
                tScan.next(add);
                parseEdge(tScan, graph);
            }

            if (tScan.hasNext(remove)) {     // remove edge
                tScan.next(remove);
                String name;
                boolean found = false;
                // check whether the input is syntactically correct
                try {
                    name = tScan.next(namePatStr);
                } catch (Exception e) {
                    System.out.println("Invalid component name - must be f.e. R1");
                    tScan.nextLine();
                    continue;
                }
                // search for the edge name in the graph
                for (Edge e : graph.edgeSet()) {
                    if (e.getName().equals(name)) {
                        e.printArt();
                        System.out.println("Do you wish to delete it? (YES/NO)");
                        while (true) {
                            if (tScan.hasNext(esc)) {
                                System.exit(1);
                            }
                            if (tScan.hasNext(yes)) {
                                tScan.next(yes);
                                graph.removeEdge(e);
                                System.out.println("Edge removed.");
                                break;
                            }
                            if (tScan.hasNext(no)) {
                                tScan.next(no);
                                break;
                            }
                            tScan.nextLine();
                        }
                        found = true;
                    }
                }
                // If an edge has been found and a decision has been made by the user the while loop is broken and the
                // text below has to be skipped.
                if (!found)
                    System.out.println("Component not found.");
            }

            if (tScan.hasNext(read)) {      // read file
                ReadUtil.read("C:\\Users\\Jakob\\Documents\\Git Repos\\NACLI\\testfiles\\netlist.cir", graph);
            }

            tScan.nextLine();
        }
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        mainInterface();
        return 0;
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

    public static void parseEdge(Scanner sc, CircuitGraph graph) {
        int source, target;
        Component type;
        String name;
        BigDecimal value;

        try {
            name = sc.next(namePatStr);
            if (!GraphUtil.isUniqueName(graph, name)) {
                System.out.println("Component name is not unique.");
                sc.nextLine();
                return;
            }
            type = Component.valueOf(name.substring(0, 1));
        } catch (Exception e) {
            System.out.println("Invalid component name - must be f.e. R1");
            sc.nextLine();
            return;
        }

        try {
            source = Integer.parseInt(sc.next(intPatStr));
        } catch (Exception e) {
            System.out.println("Invalid source vertex - must be a positive integer.");
            sc.nextLine();
            return;
        }

        try {
            target = Integer.parseInt(sc.next(intPatStr));
        } catch (Exception e) {
            System.out.println("Invalid target vertex - must be an integer.");
            sc.nextLine();
            return;
        }

        try {
            value = sc.nextBigDecimal();
        } catch (Exception e) {
            System.out.println("Invalid component value - must be a BigDecimal");
            sc.nextLine();
            return;
        }

        graph.addVertex(source);
        graph.addVertex(target);
        Edge e = graph.addEdge(source, target);
        e.setName(name);
        e.setComponentType(type);
        e.setValue(value);
        System.out.println("New connection made.");
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
