package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.shortestpath.Label;
import org.insa.algo.shortestpath.LabelStar;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

import java.util.ArrayList;
import java.util.HashMap;

public class MergeAlgorithm extends CarPoolingAlgorithm {
	protected MergeAlgorithm(CarPoolingData data) {
		super(data);
	}
	private double evalDist(Label from, Arc arc) {
		return from.getCost() + data.getCost(arc);
	}

	@Override
	protected CarPoolingSolution doRun() {
		Graph graph = getInputData().getGraph();

		Node startA = getInputData().getUser_A();
		Node startB = getInputData().getUser_B();
		Node target = getInputData().getDestination();

		HashMap<Node, Label> labels_A = new HashMap<>();
		HashMap<Node, Label> labels_B = new HashMap<>();
		HashMap<Node, MergeLabel> labels_AB = new HashMap<>();

		BinaryHeap<Label> dijkstraHeap = new BinaryHeap<>();
		BinaryHeap<Label> aStarHeap = new BinaryHeap<>();

		Label startLabel = new Label(startA, 0);
		labels_A.put(startA, startLabel);


		boolean destinationReached = false;
		while (!destinationReached && dijkstraHeap.size() > 0) {
			Label item = dijkstraHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyNodeMarked(item.getNode());
			if (item.getNode() == target) {
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {

					if (data.isAllowed(arc)) {


						Label suiv = labels_A.get(arc.getDestination());
						if (suiv == null) {
							suiv = new Label(arc.getDestination(), Double.POSITIVE_INFINITY);
							labels_A.put(arc.getDestination(), suiv);
						}


						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									dijkstraHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
									suiv.setState(Label.LabelState.VISITED);
								}
								dijkstraHeap.insert(suiv);
							}
						}
					}
				}
			}
		}

		dijkstraHeap = new BinaryHeap<>();
		destinationReached = false;

		startLabel = new Label(startB, 0);
		labels_B.put(startB, startLabel);
		dijkstraHeap.insert(startLabel);

		while (!destinationReached && dijkstraHeap.size() > 0) {
			Label item = dijkstraHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyNodeMarked(item.getNode());
			Label item_A = labels_A.get(item.getNode());
			if (item_A != null && item_A.getState() == Label.LabelState.MARKED) {
				MergeLabel mergeLabel = new MergeLabel(item.getNode(), item.getTotalCost() + item_A.getTotalCost(), getInputData(), item_A.getPrev(), item.getPrev());
				labels_AB.put(item.getNode(), mergeLabel);
				aStarHeap.insert(mergeLabel);
			}
			if (item.getNode() == target) {
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {

					if (data.isAllowed(arc)) {


						Label suiv = labels_B.get(arc.getDestination());
						if (suiv == null) {
							suiv = new Label(arc.getDestination(), Double.POSITIVE_INFINITY);
							labels_B.put(arc.getDestination(), suiv);
						}


						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									dijkstraHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
									suiv.setState(Label.LabelState.VISITED);
								}
								dijkstraHeap.insert(suiv);
							}
						}
					}
				}
			}
		}

		destinationReached = false;
		while (!destinationReached && aStarHeap.size() > 0) {
			MergeLabel item = (MergeLabel) aStarHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyNodeMarked(item.getNode());
			if (item.getNode() == target) {
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {

					if (data.isAllowed(arc)) {


						MergeLabel suiv = labels_AB.get(arc.getDestination());
						if (suiv == null) {
							suiv = new MergeLabel(arc.getDestination(), Double.POSITIVE_INFINITY, getInputData(), null, null);
							labels_AB.put(arc.getDestination(), suiv);
						}


						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									dijkstraHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
									suiv.setState(Label.LabelState.VISITED);
								}
								dijkstraHeap.insert(suiv);
							}
						}
					}
				}
			}

		}

		if (labels_AB.get(target) == null) {
			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.INFEASIBLE);
		} else {
			ArrayList<Node> pathArrayA = new ArrayList<>();
			ArrayList<Node> pathArrayB = new ArrayList<>();
			ArrayList<Node> pathArrayAB = new ArrayList<>();
			Arc cursorA;
			Arc cursorB;
			Arc cursorAB = labels_AB.get(target).getPrev();

			pathArrayAB.add(target);
			while (cursorAB != null) {
				pathArrayAB.add(0, cursorAB.getOrigin());
				cursorAB = labels_AB.get(cursorAB.getOrigin()).getPrev();
			}

			cursorA = labels_A.get(pathArrayAB.get(0)).getPrev();
			pathArrayA.add(pathArrayAB.get(0));
			while (cursorA != null) {
				pathArrayA.add(0, cursorA.getOrigin());
				cursorA = labels_A.get(cursorA.getOrigin()).getPrev();
			}

			cursorB = labels_B.get(pathArrayAB.get(0)).getPrev();
			pathArrayB.add(pathArrayAB.get(0));
			while (cursorB != null) {
				pathArrayB.add(0, cursorB.getOrigin());
				cursorB = labels_B.get(cursorB.getOrigin()).getPrev();
			}
			Path pathA;
			Path pathB;
			Path pathAB;
			if(getInputData().getMode()== AbstractInputData.Mode.LENGTH){
				 pathA = Path.createShortestPathFromNodes(graph, pathArrayA);
				 pathB = Path.createShortestPathFromNodes(graph, pathArrayB);
				 pathAB = Path.createShortestPathFromNodes(graph, pathArrayAB);
			}
			else{
				 pathA = Path.createFastestPathFromNodes(graph, pathArrayA);
				 pathB = Path.createFastestPathFromNodes(graph, pathArrayB);
				 pathAB = Path.createFastestPathFromNodes(graph, pathArrayAB);
			}

			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.OPTIMAL, pathA, pathB, pathAB);

		}
	}
}
