    888b    888        d8888  .d8888b.  888      8888888 
    8888b   888       d88888 d88P  Y88b 888        888   
    88888b  888      d88P888 888    888 888        888   
    888Y88b 888     d88P 888 888        888        888   
    888 Y88b888    d88P  888 888        888        888   
    888  Y88888   d88P   888 888    888 888        888   
    888   Y8888  d8888888888 Y88b  d88P 888        888   
    888    Y888 d88P     888  "Y8888P"  88888888 8888888 
    
    Nodal Analysis Command Line Interface by Jakob Holz 
  
# Features
- Supported component types: Current Source, Resistor, Conductor
- The program can validate the graph and automatically detect and remove floating nodals
- Print the SLE (System of Linear Equations)
- Multiple connections between two nodals are allowed
- Unlimited nodals (But Im running into trouble with limited precision for 4x4 determinants)

# Technical Keypoints
- Java 21
- Picocli to enforce a standard command-line interface
- Gradle for dependency management
- JGraphT library for graph operations
- Precise component values through BigDecimal
- Custom MathUtil implementation

# Prerequisites
1. Install Java Development Kit (JDK) 21
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or use your preferred OpenJDK distribution
   - Set JAVA_HOME environment variable to point to your JDK installation
   - Add Java's bin directory to your PATH

# Building the Project

## Building with Gradle
```bash
./gradlew build
```

The built JAR file will be located in `build/libs/NACLI-1.0-SNAPSHOT.jar`

# Running the Application

```bash
java -jar build/libs/NACLI-1.0-SNAPSHOT.jar
```

# Usage Instructions
1. Launch the application using the command above
2. Construct your network using the following command format:
   ```
   add [ComponentType][Number] [SourceNodal] [TargetNodal] [Value]
   ```
   Example: `add R1 0 1 470` creates a 470Ω resistor between nodals 0 and 1

3. Available Commands:
   - `add` - Add a new component
   - `rem` - Remove a component
   - `val` - Validate the network
   - `calc` - Calculate nodal voltages
   - `esc` - Exit the program

4. Component Types:
   - `R` - Resistor (value in Ohms)
   - `I` - Current Source (value in Amperes)
   - `G` - Conductor (value in Siemens)

# Theory of Operation
The circuit under test is manually inserted by the user using the command line interface. Control and data inputs are 
processed using regular expressions. The circuit is represented as a directed weighted graph, which is created live with
every input. The graph is validated upon request ("val" control input) or automatically when a calculation
is triggered ("cal" control input). During the validation procedure the user is prompted to fix all errors if any are detected.

Once the calculation is triggered the previously created graph is parsed into a system of linear equations (SLE) as per 
common rules of the nodal analysis. In software this is accomplished by iterating through every edge (component) connected
to every vertex (nodal) and adding the edge weight to corresponding index in the array e.g. conductance matrix (For further 
information on how this is done exactly please see the makeSLE method in the Main.java class). After the matrix has been
parsed and checked for symmetry, Cramers method is used to solve the SLE and calculate the individual nodal potentials, 
which are finally printed to the command line.

# Example Usage
```
add R1 0 1 470    # Add 470Ω resistor between nodals 0 and 1
add I1 0 1 0.01   # Add 10mA current source between nodals 0 and 1
calc              # Calculate nodal voltages
```

# Troubleshooting
Build issues:
   - Ensure Java 21 is correctly installed: `java -version`
   - Verify JAVA_HOME is correctly set
   - Try running with `./gradlew clean build`
