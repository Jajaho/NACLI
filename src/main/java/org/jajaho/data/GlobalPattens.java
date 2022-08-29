package org.jajaho.data;

import java.util.regex.Pattern;

public interface GlobalPattens {
    // Global Escape Command Pattern
    Pattern esc = Pattern.compile("esc", Pattern.CASE_INSENSITIVE);
    // Global Yes/No Patterns
    Pattern yes = Pattern.compile("yes", Pattern.CASE_INSENSITIVE);
    Pattern no = Pattern.compile("no", Pattern.CASE_INSENSITIVE);
    // Level 1 Command Patterns
    Pattern validate = Pattern.compile("val", Pattern.CASE_INSENSITIVE);        // Validate the graph
    Pattern calculate = Pattern.compile("calc", Pattern.CASE_INSENSITIVE);      // Calculate the result
    Pattern add = Pattern.compile("add", Pattern.CASE_INSENSITIVE);             // Add edge
    Pattern remove = Pattern.compile("rem", Pattern.CASE_INSENSITIVE);          // Remove edge
    Pattern show = Pattern.compile("show", Pattern.CASE_INSENSITIVE);           // Show all edges between vertices
    Pattern read = Pattern.compile("read", Pattern.CASE_INSENSITIVE);           // Read file

    // Input Pattern Strings
    String intPatStr = "0|([1-9][0-9]*)";
    String namePatStr = "[IRG](0|([1-9][0-9]*))";   // Not case-insensitive because of enum and remove command
    String doublePatStr = "^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$";
}
