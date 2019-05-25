package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.algo.carpooling.CarPoolingData;
import org.insa.graph.Node;
import org.insa.graph.Point;

public class LabelStar extends Label implements Comparable<Label> {
	protected double distToDest;
	public LabelStar(Node noeud, double cost, ShortestPathData data) {
		super(noeud,cost);
		if (data.getMode() == AbstractInputData.Mode.TIME) {
			int speed = data.getMaximumSpeed();
			if(speed<=0) {
				
				speed=data.getGraph().getGraphInformation().getMaximumSpeed();
			}
			else {
				speed=Integer.min(speed, data.getGraph().getGraphInformation().getMaximumSpeed());
			}
			this.distToDest = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(speed/3.6);
		}
		else {
			this.distToDest=Point.distance(noeud.getPoint(), data.getDestination().getPoint());
		}
	}
	public LabelStar(Node noeud,double cost,CarPoolingData data) {
		super(noeud,cost);
		if (data.getMode() == AbstractInputData.Mode.TIME) {
			int speed = data.getMaximumSpeed();
			if(speed<=0) {
				
				speed=data.getGraph().getGraphInformation().getMaximumSpeed();
			}
			else {
				speed=Integer.min(speed, data.getGraph().getGraphInformation().getMaximumSpeed());
			}
			this.distToDest = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(speed/3.6);
		}
		else {
			this.distToDest=Point.distance(noeud.getPoint(), data.getDestination().getPoint());
		}
	}
	public LabelStar(Node noeud,double cost,double heuristicValue){
		super(noeud,cost);
		this.distToDest=heuristicValue;
	}

	public double getDistToDest(){
		return this.distToDest;
	}
	//redéfinir getTotalCost dans LabelStar
	public double getTotalCost() {
		//cout depuis l'origine + cout estimé à la destination
		return this.getCost()+this.getDistToDest();
	}

	@Override
	public int compareTo(Label o) {
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
