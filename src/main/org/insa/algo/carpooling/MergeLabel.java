package org.insa.algo.carpooling;

import org.insa.algo.shortestpath.LabelStar;
import org.insa.graph.Arc;
import org.insa.graph.Node;

import java.util.ArrayList;

public class MergeLabel extends LabelStar {
	public enum MergingState{
		VOID,
		A_PATH,
		B_PATH,
		MERGED
	}
	private ArrayList<Boolean> division;
	private MergingState mergingState;
	public MergeLabel(Node noeud, double cost, CarPoolingData data,boolean prevA,boolean prevB,MergingState mergingState) {
		super(noeud, cost, data);
		this.division=new ArrayList<>();
		this.mergingState=mergingState;
	}

	public MergingState getMergingState() {
		return mergingState;
	}

	public void setMergingState(MergingState mergingState) {
		this.mergingState = mergingState;
	}
	
	
}
