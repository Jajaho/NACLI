package org.jajaho.data;

import org.jgrapht.graph.Pseudograph;

public class CircuitGraph extends Pseudograph<Integer, Edge> {

    public CircuitGraph(Class<? extends Edge> edgeClass) {
        super(edgeClass);
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
}
