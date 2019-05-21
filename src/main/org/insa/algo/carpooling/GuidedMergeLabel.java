package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.graph.Arc;
import org.insa.graph.Node;
import org.insa.graph.Point;

public class GuidedMergeLabel extends MergeLabel {
	
	
	private double costA;
	private double costB;
	private double costAT;
	private double costBT;
	public GuidedMergeLabel(Node noeud, double cost, CarPoolingData data,boolean prevA,boolean prevB,MergingState mergingState) {
		super(noeud, cost, data,prevA,prevB,mergingState);
		if (data.getMode() == AbstractInputData.Mode.TIME) {
			int speed = data.getMaximumSpeed();
			if(speed<=0) {
				
				speed=data.getGraph().getGraphInformation().getMaximumSpeed();
			}
			else {
				speed=Integer.min(speed, data.getGraph().getGraphInformation().getMaximumSpeed());
			}
			this.distToDest = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(speed/3.6);
			this.costB = Point.distance(noeud.getPoint(),data.getUser_B().getPoint())/(speed*3.6);
			this.costA=Point.distance(noeud.getPoint(),data.getUser_A().getPoint())/(speed*3.6);
			this.costAT=Point.distance(data.getDestination().getPoint(),data.getUser_A().getPoint())/(speed*3.6);
			this.costBT=Point.distance(data.getDestination().getPoint(),data.getUser_B().getPoint())/(speed*3.6);
			
		}
		else {
			this.distToDest=Point.distance(noeud.getPoint(), data.getDestination().getPoint());
			this.costB = Point.distance(noeud.getPoint(),data.getUser_B().getPoint());
			this.costA = Point.distance(noeud.getPoint(),data.getUser_A().getPoint());
			this.costAT=Point.distance(data.getDestination().getPoint(),data.getUser_A().getPoint());
			this.costBT=Point.distance(data.getDestination().getPoint(),data.getUser_B().getPoint());
		}
	}
	@Override
	public double getTotalCost() {
		//cout depuis l'origine + cout estimé à la destination
		if(this.getMergingState()==MergingState.MERGED) {
			return this.getCost()+this.getDistToDest();
		}
		else {
			if(this.getMergingState()==MergingState.A_PATH) {
				return Double.min(this.getCost()+this.distToDest+this.costB,this.getCost()+this.distToDest+this.costBT);
			}
			else {
				return Double.min(this.getCost()+this.distToDest+this.costA,this.getCost()+this.distToDest+this.costAT);
			}
		}
	}

}
