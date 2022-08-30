package org.jajaho.util;

import org.jajaho.data.CircuitGraph;
import org.jajaho.main.Main;

import java.io.File;
import java.util.Scanner;

public class ReadUtil {
    public static void read(String filePath, CircuitGraph graph) {
        try {
            File file = new File(filePath);
            Scanner fScan = new Scanner(file);
            while (fScan.hasNextLine()) {
                Main.parseEdge(fScan, graph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
