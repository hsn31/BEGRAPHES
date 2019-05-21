package org.insa.algo.carpooling;

import org.insa.algo.shortestpath.LabelStar;
import org.insa.graph.Arc;
import org.insa.graph.Node;

public class MergeLabel extends LabelStar {
	private Arc prevA;
	private Arc prevB;
	public MergeLabel(Node noeud, double cost, CarPoolingData data,Arc prevA,Arc prevB) {
		super(noeud, cost, data);
		this.prevA=prevA;
		this.prevB=prevB;
	}

	public Arc getPrevA() {
		return prevA;
	}

	public Arc getPrevB() {
		return prevB;
	}
}
