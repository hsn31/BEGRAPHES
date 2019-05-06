package org.insa.algo.shortestpath;

import org.insa.graph.Node;
import org.insa.graph.Point; //Class representing a point (position) on Earth.
import org.insa.algo.AbstractInputData;

public class LabelStar extends Label implements Comparable<Label> {


	private double distToDest;
	public LabelStar(Node noeud, double cost, ShortestPathData data) {
		super(noeud,cost);


		if (data.getMode() == AbstractInputData.Mode.LENGTH) {
			this.distToDest=Point.distance(noeud.getPoint(), data.getDestination().getPoint());
		}
		
		else {
			int vitesse = Math.max(data.getMaximumSpeed(), data.getGraph().getGraphInformation().getMaximumSpeed());
			this.distToDest = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(vitesse*1000/3600);
		}
	}
	public double getDistToDest(){
		return this.distToDest;
	}
	//red�finir getTotalCost dans LabelStar
	public double getTotalCost() {
		//co�t depuis l'origine + co�t estim� � la destination
		return this.getCost()+this.getDistToDest(); // Ici on voit le problème du static, LabelStar.cout aura la même valeur pour tous les labels
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
