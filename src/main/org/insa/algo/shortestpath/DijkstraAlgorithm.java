package org.insa.algo.shortestpath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.*;
import org.insa.graph.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {
	
	protected int nbSommetsVisites;
	protected int nbSommets;
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
        this.nbSommetsVisites = 0;
    }


	@Override
	protected ShortestPathSolution doRun() {
		boolean fin = false;
		ShortestPathData data = getInputData();
		Graph graph = data.getGraph();
		int tailleGraphe = graph.size();

		ShortestPathSolution solution = null;

		//Label

		Label tabLabels[] = new Label [tailleGraphe];

		//tas
		BinaryHeap<Label> tas = new BinaryHeap<Label>();

		//predeceurs
		Arc[] predecessorArcs = new Arc[tailleGraphe];


		//Ajout du sommet de d�part
		Label deb = newLabel(data.getOrigin(), data);
		tabLabels[deb.getNode().getId()] = deb;
		tas.insert(deb);
		deb.setInTas();
		deb.setCost(0);

		//sommets non marqu�s 
		while(!tas.isEmpty() && !fin){      	

			Label current= tas.deleteMin();
			current.setMark();
			//Destination, on s'arr�te
			if (current.getNode() == data.getDestination()) {
				fin = true;
			}
			/* Parcours des successeurs du sommet courant */
			Iterator<Arc> arc = current.getNode().iterator();
			while (arc.hasNext()) {
				Arc arcIter = arc.next();

				// On v�rifie que l'on peut r�ellement prendre cet arc
				if (!data.isAllowed(arcIter)) {
					continue;
				}

				Node successeur = arcIter.getDestination();

				/* On recupere le label correspondant au noeud dans le tableau de labels */
				Label successeurLabel = tabLabels[successeur.getId()];

				/* Si le label n'existe pas encore */
				/* Alors on le cr�e */
				if (successeurLabel == null) {
					successeurLabel = newLabel(successeur, data);
					tabLabels[successeurLabel.getNode().getId()] = successeurLabel;
					/* On incr�mente le nombre de sommets visit�s pour le test de performance */
					this.nbSommetsVisites++;
				}

				/* Si le successeur n'est pas encore marqu� */
				if (!successeurLabel.getMark()) {
					/* Si on obtient un meilleur co�t */
					/* Alors on le met � jour */

					if((successeurLabel.getTotalCost()>(current.getCost()+data.getCost(arcIter)
						+(successeurLabel.getTotalCost()-successeurLabel.getCost()))) 
						|| (successeurLabel.getCost()==Float.POSITIVE_INFINITY)){
						successeurLabel.setCost(current.getCost()+(float)data.getCost(arcIter));
						successeurLabel.setFather(current.getNode());
						// Si le label est d�j� dans le tas
						// Alors on met � jour sa position dans le tas
						if(successeurLabel.getInTas()) {
							tas.remove(successeurLabel);
						}
						// Sinon on l'ajoute dans le tas 
						else {
							successeurLabel.setInTas();
						}
						tas.insert(successeurLabel);
						predecessorArcs[arcIter.getDestination().getId()] = arcIter;
					}
				}

			}
		}

		// Destination has no predecessor, the solution is infeasible...
		if (predecessorArcs[data.getDestination().getId()] == null) {
			solution = new ShortestPathSolution(data, Status.INFEASIBLE);
		} else {

			// The destination has been found, notify the observers.
			notifyDestinationReached(data.getDestination());

			// Create the path from the array of predecessors...
			ArrayList<Arc> arcs = new ArrayList<>();
			Arc arc = predecessorArcs[data.getDestination().getId()];

			while (arc != null) {
				arcs.add(arc);
				arc = predecessorArcs[arc.getOrigin().getId()];
			}

			// Reverse the path...
			Collections.reverse(arcs);

			// Create the final solution.
			solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));

		}

		return solution;
	}
	
	// Cr�e et retourne le Label correspondant au Node
	protected Label newLabel(Node node, ShortestPathData data) {
		return new Label(node);
	}
	
	// Retourne le nombre de sommets visit�s
	public int getNbSommetsVisites() {
		return this.nbSommetsVisites;
	}
}
