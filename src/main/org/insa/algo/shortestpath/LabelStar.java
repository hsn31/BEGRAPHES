package org.insa.algo.shortestpath;

import org.insa.graph.Node;
import org.insa.graph.Point; //Class representing a point (position) on Earth.
import org.insa.algo.AbstractInputData;

public class LabelStar extends Label implements Comparable<Label> {

	private static double cout; // Static implique une variable globale commune à tous les membres de la classe LabelStar, pas forcément ce qu'on veut ici
	private Node target;
	public LabelStar(Node noeud, ShortestPathData data) {
		super(noeud, cout); //Cout peut ne pas être initialisé ici

		if (data.getMode() == AbstractInputData.Mode.LENGTH) {
			LabelStar.cout = Point.distance(noeud.getPoint(), data.getDestination().getPoint()); //this.cout ?
		}
		
		else {
			int vitesse = Math.max(data.getMaximumSpeed(), data.getGraph().getGraphInformation().getMaximumSpeed());
			LabelStar.cout = Point.distance(noeud.getPoint(),data.getDestination().getPoint())/(vitesse*1000.0f/3600.0f); //  idem pour this.cout, et peut être une méthode déjà prévu?
		}
	}

	//red�finir getTotalCost dans LabelStar
	public double getTotalCost() {
		//co�t depuis l'origine + co�t estim� � la destination
		return LabelStar.cout+this.getCost(); // Ici on voit le problème du static, LabelStar.cout aura la même valeur pour tous les labels
	}

	public double getTotalCostBis(){
		return this.getCost()+Point.distance(this.getNode().getPoint(),this.target.getPoint()); //Proposition
	}
	
}
