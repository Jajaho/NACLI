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

/**
 * Main application class for the Nodal Analysis Command Line Interface (NACLI).
 * This class provides a command-line interface for electrical circuit analysis using nodal analysis.
 * It allows users to construct and analyze electrical circuits by adding components between nodes
 * and calculating node voltages using nodal analysis methods.
 */
@CommandLine.Command(
        name = "nacli",
        description = "Nodal Analysis Command Line Interface",
        version = "nacli 0.1.0",
        mixinStandardHelpOptions = true  // Adds --help and --version options
)
public class Main implements Callable<Integer> {

    // Global scanner for reading user input from terminal
    static Scanner tScan = new Scanner(System.in);

    /**
     * Application entry point. Initializes the command-line interface using picocli.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Main interface loop that handles user interaction with the circuit analysis tool.
     * Provides commands for:
     * - Adding/removing components
     * - Validating the circuit
     * - Calculating node voltages
     * - Reading circuit definitions from files
     */
    public void mainInterface() {
        // Initialize the circuit graph - allows parallel edges between nodes
        CircuitGraph graph = new CircuitGraph(Edge.class);

        printStartupMsg();

        while (true) {
            // Handle escape command - exit program
            if (tScan.hasNext(esc)) {
                tScan.next(esc);
                System.exit(1);
            }

            // Handle validate command - check circuit validity
            if (tScan.hasNext(validate)) {
                tScan.next(validate);
                GraphUtil.validateGraph(graph, tScan);
            }

            // Handle calculate command - perform nodal analysis
            if (tScan.hasNext(calculate)) {
                tScan.next(calculate);
                // Validate before calculation
                if (!GraphUtil.validateGraph(graph, tScan)) {
                    System.out.println("Network invalid - calculation aborted.");
                    tScan.nextLine();
                    continue;
                }
                
                // Create and display system of linear equations
                Sle sle = new Sle(graph);
                sle.print();

                // Verify matrix symmetry before solving
                if (!MathUtil.isAxisSymmetric(sle.getA())) {
                    System.out.println("Matrix is not symmetric - calculation aborted.");
                    tScan.nextLine();
                    continue;
                }
                
                // Solve and display node voltages
                BigDecimal[] phis = sle.solve();
                System.out.println("Voltages at nodals (referenced to nodal 0):");
                for (int i = 1; i - 1 < phis.length; i++) {
                    System.out.println("V" + i + "= " + phis[i - 1].toEngineeringString() + " V");
                }
            }

            // Handle add command - add new component
            if (tScan.hasNext(add)) {
                tScan.next(add);
                parseEdge(tScan, graph);
            }

            // Handle remove command - remove existing component
            if (tScan.hasNext(remove)) {
                tScan.next(remove);
                String name;
                boolean found = false;
                
                // Validate component name syntax
                try {
                    name = tScan.next(namePatStr);
                } catch (Exception e) {
                    System.out.println("Invalid component name - must be f.e. R1");
                    tScan.nextLine();
                    continue;
                }
                
                // Search for component and handle deletion
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
                if (!found)
                    System.out.println("Component not found.");
            }

            // Handle read command - import circuit from file
            if (tScan.hasNext(read)) {
                ReadUtil.read("C:\\Users\\Jakob\\Documents\\Git Repos\\NACLI\\testfiles\\netlist.cir", graph);
            }

            tScan.nextLine();
        }
    }

    @Override
    public Integer call() throws Exception {
        mainInterface();
        return 0;
    }

    /**
     * Displays the startup message with ASCII art logo and basic usage instructions
     */
    private static void printStartupMsg() {
        // ASCII art and usage instructions
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

    /**
     * Parses user input to create a new circuit component (edge)
     * Format: <type><number> <source> <target> <value>
     * Example: R1 0 1 470 (resistor from node 0 to 1 with 470 ohms)
     *
     * @param sc Scanner containing the input to parse
     * @param graph Circuit graph to add the component to
     */
    public static void parseEdge(Scanner sc, CircuitGraph graph) {
        int source, target;
        Component type;
        String name;
        BigDecimal value;

        // Parse and validate component name (e.g., R1, I2)
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

        // Parse source node number
        try {
            source = Integer.parseInt(sc.next(intPatStr));
        } catch (Exception e) {
            System.out.println("Invalid source vertex - must be a positive integer.");
            sc.nextLine();
            return;
        }

        // Parse target node number
        try {
            target = Integer.parseInt(sc.next(intPatStr));
        } catch (Exception e) {
            System.out.println("Invalid target vertex - must be an integer.");
            sc.nextLine();
            return;
        }

        // Parse component value
        try {
            value = sc.nextBigDecimal();
        } catch (Exception e) {
            System.out.println("Invalid component value - must be a BigDecimal");
            sc.nextLine();
            return;
        }

        // Add the component to the circuit
        graph.addVertex(source);
        graph.addVertex(target);
        Edge e = graph.addEdge(source, target);
        e.setName(name);
        e.setComponentType(type);
        e.setValue(value);
        System.out.println("New connection made.");
    }

    /**
     * Utility method to parse user input against a pattern
     * @param p Pattern to match against
     * @return Parsed integer or null if parsing fails
     */
    private static Integer parseUserInput(Pattern p) {
        Integer res;
        try {
            res = Integer.parseInt(tScan.findInLine(p));
        } catch (NumberFormatException e) {
            res = null;
        }
        return res;
    }

    /**
     * Prompts user to define the ground node (reference node) for the circuit
     * @param graph Circuit graph
     * @return Selected ground node number
     */
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