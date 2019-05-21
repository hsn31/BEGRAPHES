package org.insa.algo.carpooling;

import org.insa.algo.shortestpath.LabelStar;
import org.insa.graph.Arc;
import org.insa.graph.Node;

public class MergeLabel extends LabelStar {
	public enum MergingState{
		VOID,
		A_PATH,
		B_PATH,
		MERGED
	}
	private Arc prevA;
	private Arc prevB;
	private MergingState mergingState;
	public MergeLabel(Node noeud, double cost, CarPoolingData data,Arc prevA,Arc prevB,MergingState mergingState) {
		super(noeud, cost, data);
		this.prevA=prevA;
		this.prevB=prevB;
		this.mergingState=mergingState;
	}

	public Arc getPrevA() {
		return prevA;
	}

	public Arc getPrevB() {
		return prevB;
	}

	public MergingState getMergingState() {
		return mergingState;
	}

	public void setMergingState(MergingState mergingState) {
		this.mergingState = mergingState;
	}
	
	
}
