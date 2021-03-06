package org.insa.algo.shortestpath;

import org.insa.algo.AbstractSolution;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

import java.util.ArrayList;
import java.util.HashMap;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	public DijkstraAlgorithm(ShortestPathData data){
		super(data);
	}

	protected Label newLabel(Node node, double cost) {
		return new Label(node, cost);
	}

	private double evalDist(Label from, Arc arc) {
		return from.getCost() + data.getCost(arc);
	}

	@Override
	protected ShortestPathSolution doRun() {
		ShortestPathData data = getInputData();
		Graph graph = data.getGraph();

		ShortestPathSolution solution;
		AbstractSolution.Status status = AbstractSolution.Status.INFEASIBLE;
		Path solutionPath;
		ArrayList<Node> nodePath = new ArrayList<>();

		if(data.getOrigin() == data.getDestination()){
			status = AbstractSolution.Status.OPTIMAL;
			nodePath.add(data.getOrigin());
			solutionPath = Path.createShortestPathFromNodes(graph,nodePath);
			return new ShortestPathSolution(data, status, solutionPath);

		}

		HashMap<Node, Label> labels = new HashMap<>();
		BinaryHeap<Label> binaryHeap = new BinaryHeap<>();
		Label startLabel = new Label(data.getOrigin(),0);
		labels.put(data.getOrigin(),startLabel);
		binaryHeap.insert(labels.get(data.getOrigin()));
		notifyOriginProcessed(data.getOrigin());

		boolean destinationReached = false;
		while (binaryHeap.size() > 0 && !destinationReached) {
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
						if(suiv==null){
							suiv=newLabel(arc.getDestination(),Double.POSITIVE_INFINITY);
							labels.put(arc.getDestination(),suiv);
						}

						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									binaryHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
									suiv.setState(Label.LabelState.VISITED);
								}
								binaryHeap.insert(suiv);
							}
						}

					}
				}
			}
		}
		Label cursor = labels.get(data.getDestination());
		nodePath.add(data.getDestination());
		while (cursor != null && cursor.getPrev() != null) {
			//Attention c'est un add
			nodePath.add(0, cursor.getPrev().getOrigin());
			cursor = labels.get(cursor.getPrev().getOrigin());
			//System.out.println("Path constructing"); TEST
		}
		if (nodePath.size() > 1) { // si on a bien atteint la destination ou si origine et destination étaient identiques
			status = AbstractSolution.Status.OPTIMAL;
			solutionPath = Path.createShortestPathFromNodes(graph, nodePath);
			solution = new ShortestPathSolution(data, status, solutionPath);
		} else {
			solution = new ShortestPathSolution(data, status);
		}
		return solution;
	}


}

