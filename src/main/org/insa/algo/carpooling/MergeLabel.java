package org.insa.algo.carpooling;

import org.insa.algo.shortestpath.LabelStar;
import org.insa.graph.Node;

import java.util.ArrayList;

public class MergeLabel extends LabelStar {
	public enum MergingState{
		VOID,
		A_PATH,
		B_PATH,
		MERGED
	}
	private ArrayList<Double> individualCosts;
	private MergingState mergingState;
	public MergeLabel(Node noeud, double cost, CarPoolingData data,MergingState mergingState){
		super(noeud, cost, data);
		this.individualCosts =new ArrayList<>();
		for(int i=0;i<data.getNbUsers();i++){
			individualCosts.add(Double.NEGATIVE_INFINITY);
		}
		this.mergingState=mergingState;

	}

	public MergingState getMergingState() {
		return mergingState;
	}

	public void setMergingState(MergingState mergingState) {
		this.mergingState = mergingState;
	}

	public void setIndividualCost(int i,double c){
		this.individualCosts.set(i,c);
	}
	public double getIndiviualCostFor(int i){
		return this.individualCosts.get(i);

	}
	public boolean hasDefinedCostFor(int i){
		return this.individualCosts.get(i)!=Double.NEGATIVE_INFINITY;
	}
	
}
