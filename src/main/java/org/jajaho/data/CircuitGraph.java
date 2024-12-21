package org.jajaho.data;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.Pseudograph;

import java.util.HashSet;
import java.util.Set;

public class CircuitGraph extends Pseudograph<Integer, Edge> {

    /**
     * Constructs a new CircuitGraph
     * @param edgeClass The class used for edges (must be Edge or a subclass)
     */
    public CircuitGraph(Class<? extends Edge> edgeClass) {
        super(edgeClass);
    }

    /**
     * TODO: Implement custom toString() method
     * Current implementation relies on parent class's toStringFromSets method
     */
    @Override
    public String toString() {
        /*
        StringBuilder vertices = new StringBuilder("[");
        for ( V vertex : vertexSet()) {
            if (vertexSet().iterator().hasNext())
                vertices.append(vertex).append(", ");
            else
                vertices.append(vertex).append(" ]");
        }

        StringBuilder edges = new StringBuilder("[");
        for (E edge : edgeSet()) {
            if (vertexSet().iterator().hasNext())
                vertices.append(edge).append(", ");
            else
                vertices.append(edge).append(" ]");
        }

        return vertices.append("$$").append(edges).toString();
         */
        return toStringFromSets(vertexSet(), edgeSet(), getType().isDirected());
    }

    /**
     * Gets the vertex at the other end of an edge from a given vertex
     * This is useful for traversing the circuit graph and analyzing connections
     *
     * @param vertex The starting vertex
     * @param edge The edge to traverse
     * @return The vertex at the other end of the edge, or null if the vertex isn't connected to the edge
     */
    public Integer getOppositeOf(Integer vertex, Edge edge) {
        if (getEdgeTarget(edge).equals(vertex))
            return getEdgeSource(edge);
        if (getEdgeSource(edge).equals(vertex))
            return getEdgeTarget(edge);
        return null;
    }

    public Set<Integer> getAcyclicVertices() {
        CycleDetector cDetect = new CycleDetector(this);
        Set<Integer> set = new HashSet<>();

        for (Integer vertex : vertexSet()) {
            if (!cDetect.detectCyclesContainingVertex(vertex)) {
                set.add(vertex);
            }
        }
        return set;
    }

    // AbstractBaseGraph uses private IntrusiveEdgesSpecifics to set/get EdgeWeight
    public void setEdgeType(Edge e, Component type) {
        if (e == null) {
            throw new NullPointerException();
        } else {
            e.setComponentType(type);
        }
    }

    public Component getEdgeType(Edge e) {
        if (e == null) {
            throw new NullPointerException();
        } else {
            return e.getComponentType();
        }
    }
}
