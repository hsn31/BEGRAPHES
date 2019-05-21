package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class LabelCovoit extends Label {
	public enum CovoitState{
		NONE,
		A,
		B,
		BOTH
	}
	private CovoitState covoitState;
	
	
	
	
	public LabelCovoit(Node node, double cost) {
		super(node, cost);
	}
	
	@Override
	public int compareTo(Label o) {
		assert o instanceof LabelStar;
		if(o.getTotalCost()>this.getTotalCost()){
			return -1;
		}
		else if(o.getTotalCost()==this.getTotalCost()){
			return 0;
		}
		else{
			return 1;
		}
	}

}
