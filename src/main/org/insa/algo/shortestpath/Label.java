package org.insa.algo.shortestpath;
import org.insa.graph.Arc;
import org.insa.graph.Node;

public class Label implements Comparable<Label> {
	private Node node;
	private double cost;
	private LabelState state;
	private Arc prev;

	public LabelState getState() {
		return state;
	}

	public void setState(LabelState state) {
		this.state = state;
	}

	public Arc getPrev() {
		return prev;
	}

	public void setPrev(Arc prev) {
		this.prev = prev;
	}

	public enum LabelState{
		VISITED,
		UNVISITED,
		MARKED;

	}

	public Label(Node node, double cost) {
		this.node = node;
		this.cost = cost;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public int compareTo(Label o) {
		if(this.getCost()<o.getCost()){
			return -1;
		}
		else if(this.getCost()==o.getCost()){
			return 0;
		}
		else{
			return 1;
		}
	}


}
