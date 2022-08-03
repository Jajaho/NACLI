package org.jajaho2.data;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
    private Component componentType;

    public Edge() {
        super();
    }

    public Component getComponentType() {
        return componentType;
    }

    public void setComponentType(Component type) {
        this.componentType = type;
    }

    @Override // TODO
    public String toString() {
        return super.toString();
    }

    @Override // TODO
    public Object clone() {
        return super.clone();
    }
}
