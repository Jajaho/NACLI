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
- The program can validate the graph and automatically remove floating nodals.
- Print the SLE
- Multiple connections between two nodals are allowed.
- Unlimited nodals

# Technical Keypoints
- GraalVM 11 JDK
- GraalVM 20.02 compiler
- Picocli
- Gradle 7.1 for dependency management
- Customised JGraphT library
- Precise component values through BigDecimal
- Custom MathUtil.java

# Theory of Operation
The circuit under test is manually inserted by the user using the command line interface. Control and data inputs are 
processed using regular expressions. The circuit is represented as a directed weighted graph, which is created live with
every input. The graph is validated upon request ("val" control input) or automatically before every compute if a calculation
is triggered ("cal" control input). During the validation procedure the user is prompted to fix all errors if any are detected.
Once the calculation is triggered the previously created graph is parsed into a system of linear equations (SLE) as per 
common rules of the nodal analysis. In software this is accomplished by iterating through every edge (component) connected
to every vertex (nodal) and adding the edge weight to corresponding index in the array e.g. conductance matrix (For further 
information on how this is done exactly please see the makeSLE method in the Main.java class). After the matrix has been 
parsed and checked for symmetry, Cramers method is used to solve the SLE and calculate the individual nodal potentials.