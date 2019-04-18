package org.insa.algo.shortestpath;

import java.util.ArrayList;

import org.insa.algo.AbstractSolution;
import org.insa.algo.utils.BinaryHeap;

import java.util.HashMap;
import org.insa.graph.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
	}

	protected double heuristic(Label from, Label to, Arc arc) {
		return from.getCost() + arc.getLength();
	}

	@Override
	protected ShortestPathSolution doRun() {
		ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;
		AbstractSolution.Status status = AbstractSolution.Status.INFEASIBLE;
		Path solutionPath;
		ArrayList<Node> nodePath = new ArrayList<>();
		Graph graph = data.getGraph();
		HashMap<Node, Label> labels = new HashMap<>();
		BinaryHeap<Label> binaryHeap = new BinaryHeap<>();
		for (Node node : graph.getNodes()) {
			Label label = new Label(node, Double.POSITIVE_INFINITY);
			if (node == data.getOrigin()) {
				label.setCost(0);
				binaryHeap.insert(label);
				notifyOriginProcessed(data.getOrigin());
			}
			labels.put(node, label);
		}

		boolean destinationReached = false;
		while (binaryHeap.size() > 0 && destinationReached != true) {
			Label item = binaryHeap.findMin();
			binaryHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyNodeMarked(item.getNode());
			if (item.getNode() == data.getDestination()) {
				notifyDestinationReached(item.getNode());
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {

					if (data.isAllowed(arc)) {

						Label suiv = labels.get(arc.getDestination());
						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = heuristic(item, suiv, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									binaryHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
								}
								binaryHeap.insert(suiv);
							}
						}
					}
				}
			}
		}
		//System.out.println("Test Sortie de Boucle");  TEST
		Label cursor = labels.get(data.getDestination());

		nodePath.add(data.getDestination());
		while (cursor.getPrev() != null) {
			//Attention c'est un add
			nodePath.add(0, cursor.getPrev().getOrigin());
			cursor = labels.get(cursor.getPrev().getOrigin());
			//System.out.println("Path constructing"); TEST
		}
		if (nodePath.size() > 1) {
			status = AbstractSolution.Status.OPTIMAL;
			solutionPath = Path.createShortestPathFromNodes(graph, nodePath);
			solution = new ShortestPathSolution(data, status, solutionPath);
		} else {
			solution = new ShortestPathSolution(data, status);
		}
		return solution;
	}




}

