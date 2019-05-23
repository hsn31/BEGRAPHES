package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.shortestpath.Label;
import org.insa.algo.shortestpath.Label.LabelState;
import org.insa.algo.shortestpath.LabelStar;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GuidedMergeAlgorithm extends CarPoolingAlgorithm {

	private static final int COMPUTING_A = 0;
	private static final int COMPUTING_B = 1;
	private static final int COMPUTING_AB = 2;


	protected GuidedMergeAlgorithm(CarPoolingData data) {
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

		double directCostA = Point.distance(startA.getPoint(), target.getPoint());
		double directCostB = Point.distance(startB.getPoint(), target.getPoint());


		//System.out.println("MERGE LAUNCHED");
		//System.out.println("A : " + startA.getId());
		//System.out.println("B : " + startB.getId());
		//System.out.println("T : " + target.getId());


		HashMap<Node, Label> labels_A = new HashMap<>();
		HashMap<Node, Label> labels_B = new HashMap<>();
		HashMap<Node, Label> labels_AB = new HashMap<>();

		BinaryHeap<Label> dijkstrAStarHeap = new BinaryHeap<>();


		Label startLabelA = new LabelStar(startA, 0, directCostB);
		Label startLabelB = new LabelStar(startB, 0, directCostA);
		labels_A.put(startA, startLabelA);
		dijkstrAStarHeap.insert(startLabelA);
		labels_B.put(startB, startLabelB);
		dijkstrAStarHeap.insert(startLabelB);


		boolean destinationReached = false;
		boolean destinationReachedA = false;
		boolean destinationReachedB = false;
		int computingState = 0;


		//DIJKSTRA ATB
		System.out.println("\nDIJKSTRA ABT LAUNCHED");
		while (!destinationReached && (dijkstrAStarHeap.size() > 0)) {
			HashMap<Node, Label> selectedLabels = null;

			Label item = dijkstrAStarHeap.deleteMin();
			System.out.println("Computing "+item.getNode().getId());
			Label item_A = labels_A.get(item.getNode());
			Label item_B = labels_B.get(item.getNode());
			Label item_AB = labels_AB.get(item.getNode());
			if ((item==item_A && !destinationReachedA) || (item==item_B && !destinationReachedB) || item==item_AB) {
				System.out.println("Valid");
				//Merging check
				if (item == item_A && item_B != null && item_B.getState() == LabelState.MARKED) {
					MergeLabel label = new MergeLabel(item.getNode(), labels_A.get(item.getNode()).getCost() + labels_B.get(item.getNode()).getCost(), getInputData(), MergeLabel.MergingState.MERGED);
					label.setState(LabelState.VISITED);
					labels_AB.put(item.getNode(), label);
					dijkstrAStarHeap.insert(label);
					notifyNodeMerged(item.getNode());

				} else if (item == item_B && item_A != null && item_A.getState() == LabelState.MARKED) {
					MergeLabel label = new MergeLabel(item.getNode(), labels_A.get(item.getNode()).getCost() + labels_B.get(item.getNode()).getCost(), getInputData(), MergeLabel.MergingState.MERGED);
					label.setState(LabelState.VISITED);
					labels_AB.put(item.getNode(), label);
					dijkstrAStarHeap.insert(label);
					notifyNodeMerged(item.getNode());
				}

				item.setState(LabelState.MARKED);

				if (item == item_A) {
					computingState = COMPUTING_A;
					selectedLabels = labels_A;
					System.out.println("FROM A");
				} else if (item == item_B) {
					computingState = COMPUTING_B;
					selectedLabels = labels_B;
					System.out.println("FROM B");
				} else if (item == item_AB) {
					computingState = COMPUTING_AB;
					selectedLabels = labels_AB;
					System.out.println("FROM O");
				}
				if (computingState == COMPUTING_A) {
					notifyNodeMarked(item.getNode());
				} else if (computingState == COMPUTING_B) {
					notifyNodeMarked(item.getNode());
				} else if (computingState == COMPUTING_AB) {
					notifyMergedNodeMarked(item.getNode());
				}

				if (item.getNode() == target) {
					if (computingState == COMPUTING_AB) {
						destinationReached = true;
					} else if (computingState == COMPUTING_A) {
						destinationReachedA = true;
						System.out.println("REACHED A");

					} else if (computingState == COMPUTING_B) {
						destinationReachedB = true;
						System.out.println("REACHED B");
					}


				} else  {
					for (Arc arc : item.getNode().getSuccessors()) {
						if (data.isAllowed(arc)) {
							LabelStar suiv = (LabelStar) selectedLabels.get(arc.getDestination());

							if (suiv == null) {
								if (computingState == COMPUTING_A) {
									suiv = new LabelStar(arc.getDestination(), Double.POSITIVE_INFINITY, directCostB);
								} else if (computingState == COMPUTING_B) {
									suiv = new LabelStar(arc.getDestination(), Double.POSITIVE_INFINITY, directCostA);
								} else {
									suiv = new MergeLabel(arc.getDestination(), Double.POSITIVE_INFINITY, getInputData(), MergeLabel.MergingState.MERGED);
								}
								selectedLabels.put(arc.getDestination(), suiv);
							}


							if (suiv.getState() != Label.LabelState.MARKED) {
								double d = evalDist(item, arc);
								if (d < suiv.getCost()) {
									suiv.setCost(d);
									suiv.setPrev(arc);
									if (suiv.getState() == Label.LabelState.VISITED) {
										dijkstrAStarHeap.remove(suiv);
									} else {
										notifyNodeReached(suiv.getNode());
										suiv.setState(Label.LabelState.VISITED);
									}
									dijkstrAStarHeap.insert(suiv);
								}

							}
						}


					}
				}
			}
		}
		System.out.println("DIJKSTRASTAR FINISHED");
		System.out.println("Direct cost A "+directCostA);
		System.out.println("Direct cost B "+directCostB);


		if (labels_AB.get(target) == null) {
			System.out.println("IMPOSSIBLE");
			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.INFEASIBLE);
		} else {
			//System.out.println("Constructing path");
			ArrayList<Node> pathArrayA = new ArrayList<>();
			ArrayList<Node> pathArrayB = new ArrayList<>();
			ArrayList<Node> pathArrayAB = new ArrayList<>();
			Arc cursorA;
			Arc cursorB;
			Arc cursorAB = labels_AB.get(target).getPrev();
			//System.out.println("Constructing COM");
			pathArrayAB.add(target);
			while (cursorAB != null) {
				pathArrayAB.add(0, cursorAB.getOrigin());
				cursorAB = labels_AB.get(cursorAB.getOrigin()).getPrev();
			}

			cursorA = labels_A.get(pathArrayAB.get(0)).getPrev();
			pathArrayA.add(pathArrayAB.get(0));
			//System.out.println("Constructing A");
			while (cursorA != null) {
				pathArrayA.add(0, cursorA.getOrigin());
				cursorA = labels_A.get(cursorA.getOrigin()).getPrev();
			}

			cursorB = labels_B.get(pathArrayAB.get(0)).getPrev();
			pathArrayB.add(pathArrayAB.get(0));
			//System.out.println("Constructing B");
			while (cursorB != null) {
				pathArrayB.add(0, cursorB.getOrigin());
				cursorB = labels_B.get(cursorB.getOrigin()).getPrev();
			}
			Path pathA;
			Path pathB;
			Path pathAB;
			if (getInputData().getMode() == AbstractInputData.Mode.LENGTH) {
				pathA = Path.createShortestPathFromNodes(graph, pathArrayA);
				pathB = Path.createShortestPathFromNodes(graph, pathArrayB);
				pathAB = Path.createShortestPathFromNodes(graph, pathArrayAB);
			} else {
				pathA = Path.createFastestPathFromNodes(graph, pathArrayA);
				pathB = Path.createFastestPathFromNodes(graph, pathArrayB);
				pathAB = Path.createFastestPathFromNodes(graph, pathArrayAB);
			}

			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.OPTIMAL, pathA, pathB, pathAB);

		}
	}


}
