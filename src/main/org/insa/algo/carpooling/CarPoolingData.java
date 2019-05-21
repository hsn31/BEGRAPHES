package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.ArcInspector;
import org.insa.graph.Graph;
import org.insa.graph.Node;

public class CarPoolingData extends AbstractInputData {
    private Node user_A;
    private Node user_B;
    private Node destination;
    public CarPoolingData(Graph graph, Node user_A,Node user_B,Node destination,ArcInspector arcFilter) {
        super(graph, arcFilter);
        this.user_A=user_A;
        this.user_B=user_B;
        this.destination=destination;
    }


    public Node getUser_A() {
        return user_A;
    }

    public Node getUser_B() {
        return user_B;
    }

    public Node getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Carpooling from #" + user_A.getId() + " and #" + user_B.getId()+" to #" + destination.getId() + " ["
                + this.getArcInspector().toString().toLowerCase() + "]";
    }
}
