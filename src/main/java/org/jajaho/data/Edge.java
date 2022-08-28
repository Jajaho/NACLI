package org.jajaho.data;

import org.jgrapht.graph.DefaultEdge;

import java.math.BigDecimal;

public class Edge extends DefaultEdge {
    private String name;
    private BigDecimal value;
    private Component componentType;

    public Edge() {
        super();
    }

    //--------- Getter & Setter ------------

    public Component getComponentType() {
        return componentType;
    }

    public void setComponentType(Component type) {
        this.componentType = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
