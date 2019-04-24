package org.insa.algo.shortestpath;

import org.insa.graph.Node;
import org.insa.graph.Point; //Class representing a point (position) on Earth.
import org.insa.algo.AbstractInputData;

public class LabelStar extends Label implements Comparable<Label> {

	private static double cout;

	public LabelStar(Node noeud, ShortestPathData data) {
		super(noeud, cout);

		if (data.getMode() == AbstractInputData.Mode.LENGTH) {
			LabelStar.cout = Point.distance(noeud.getPoint(), data.getDestination().getPoint());
		}
		
		else {
			int vitesse = Math.max(data.getMaximumSpeed(), data.getGraph().getGraphInformation().getMaximumSpeed());
			LabelStar.cout = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(vitesse*1000.0f/3600.0f);
		}
	}

	//redéfinir getTotalCost dans LabelStar
	public double getTotalCost() {
		//coût depuis l'origine + coût estimé à la destination
		return LabelStar.cout+this.getCost();
	}
	
}
