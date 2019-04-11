package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.algo.AbstractSolution.Status;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class Label {
	protected int current;
	protected boolean marque;
	protected double cost;
	protected Arc prev;
	protected Graph graph;
	protected Node[] nodeArray;
	

	public Label(int current, boolean marque, double cost, Arc prev,Graph graph,Node[] nodeArray) {
		super();
		this.current = current;
		this.marque = marque;
		this.cost = cost;
		this.prev = prev;
		this.graph=graph;
		this.nodeArray=nodeArray;
	}
	
	public double getcost() {
		return this.cost;
	}
	
}
