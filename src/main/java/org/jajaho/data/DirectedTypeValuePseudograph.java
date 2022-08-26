package org.jajaho.data;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class DirectedTypeValuePseudograph extends DirectedWeightedPseudograph<Integer, Edge> {

    public DirectedTypeValuePseudograph(Class<? extends Edge> edgeClass) {
        super(edgeClass);
    }

    public DirectedTypeValuePseudograph(Supplier<Integer> vertexSupplier, Supplier<Edge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);
    }

    @Override // TODO
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
        return toStringFromSets(vertexSet(), edgeSet(), getType().isDirected());   // from super.toString()
    }

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
