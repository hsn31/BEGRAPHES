package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.ArcInspector;
import org.insa.graph.Graph;
import org.insa.graph.Node;

import java.util.ArrayList;

public class CarPoolingData extends AbstractInputData {


    private ArrayList<Node> users;
    private Node destination;
    public CarPoolingData(Graph graph, Node user_A,Node user_B,Node destination,ArcInspector arcFilter) {
        super(graph, arcFilter);
        this.users=new ArrayList<>();
        this.users.add(user_A);
        this.users.add(user_B);
        this.destination=destination;
    }
    public CarPoolingData(Graph graph, ArrayList<Node> users,Node destination,ArcInspector arcFilter) {
        super(graph, arcFilter);
        this.users=users;
        this.destination=destination;
    }

    public Node getUser_A() {
        return this.users.get(0);
    }

    public Node getUser_B() {
        return this.users.get(1);
    }

    public Node getDestination() {
        return this.destination;
    }
    public ArrayList<Node> getUsers() {
        return users;
    }

    public int getNbUsers(){
        return this.users.size();
    }

    @Override
    public String toString() {
        return "Carpooling from #" + users.get(0).getId() + " and #" + users.get(1).getId()+" to #" + destination.getId() + " ["
                + this.getArcInspector().toString().toLowerCase() + "]";
    }
}
