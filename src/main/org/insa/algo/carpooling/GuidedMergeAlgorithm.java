package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractInputData.Mode;
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


	public GuidedMergeAlgorithm(CarPoolingData data) {
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








		//Initialize Dijkstra A and B
		BinaryHeap<Label> dijkstrAStarHeap = new BinaryHeap<>();//One common heap to all three algorithms

		HashMap<Node, Label> labels_A = new HashMap<>();
		HashMap<Node, Label> labels_B = new HashMap<>();
		double directCostA = Point.distance(startA.getPoint(), target.getPoint());
		double directCostB = Point.distance(startB.getPoint(), target.getPoint());
		if(getInputData().getMode()==Mode.TIME) {
			double speed = Integer.min(getInputData().getMaximumSpeed(), graph.getGraphInformation().getMaximumSpeed());
			if(speed<=0) {
				speed=130;
			}
			directCostA=directCostA*3.6/speed;
			directCostB=directCostB*3.6/speed;
		}
		
		Label startLabelA = new LabelStar(startA, 0, directCostB); //All A labels have constant heuristic cost set to euclidian distance BT
		Label startLabelB = new LabelStar(startB, 0, directCostA); //All B labels have constant heuristic cost set to euclidian distance AT
		labels_A.put(startA, startLabelA);
		dijkstrAStarHeap.insert(startLabelA);
		labels_B.put(startB, startLabelB);
		dijkstrAStarHeap.insert(startLabelB);
		boolean destinationReachedA = false;
		boolean destinationReachedB = false;

		//Initialize AStar OT
		HashMap<Node, Label> labels_AB = new HashMap<>();
		boolean destinationReached = false;

		//Get track of wich algorithm is computing during loops iterations
		int computingState = 0;


		//START DIJKSTRA A,DIJKSTRA B,ASTAR OT
		while (!destinationReached && (dijkstrAStarHeap.size() > 0)) {
			HashMap<Node, Label> selectedLabels = null;

			Label item = dijkstrAStarHeap.deleteMin(); //Find the label across all three sets with minimal heuristic
			Label item_A = labels_A.get(item.getNode());//Get the corresponding label for this ndoe in A
			Label item_B = labels_B.get(item.getNode());//Get the corresponding label for this ndoe in B
			Label item_AB = labels_AB.get(item.getNode());//Get the corresponding label for this ndoe in AB
			if ((item==item_A && !destinationReachedA) || (item==item_B && !destinationReachedB) || item==item_AB) {//Do not continue A or B once T is respectively reached

				if (item == item_A && item_B != null && item_B.getState() == LabelState.MARKED) {//Merging situation A->B check
					if(item_AB==null) {
						item_AB = new MergeLabel(item.getNode(), Double.POSITIVE_INFINITY, getInputData(), MergeLabel.MergingState.MERGED);
						labels_AB.put(item.getNode(), item_AB);
					}
					if(item_A.getCost() + item_B.getCost()<item_AB.getCost()) {//Checking interest of merging
						//Reset label state even if already marked(recompute the all path)
						item_AB.setState(LabelState.VISITED);
						item_AB.setCost(item_A.getCost() + item_B.getCost());
						item_AB.setPrev(null);
						dijkstrAStarHeap.insertOrUpdate(item_AB);
						notifyNodeMerged(item.getNode());
						
						
					}
					
					

				} else if (item == item_B && item_A != null && item_A.getState() == LabelState.MARKED) {//Merging situation B->A check
					if(item_AB==null) {
						item_AB = new MergeLabel(item.getNode(), Double.POSITIVE_INFINITY, getInputData(), MergeLabel.MergingState.MERGED);
						labels_AB.put(item.getNode(), item_AB);
					}
					if(item_A.getCost() + item_B.getCost()<item_AB.getCost()) {
						item_AB.setState(LabelState.VISITED);
						item_AB.setCost(item_A.getCost() + item_B.getCost());
						item_AB.setPrev(null);
						dijkstrAStarHeap.insertOrUpdate(item_AB);
						notifyNodeMerged(item.getNode());
						
						
					}
				}

				item.setState(LabelState.MARKED);

				//Could be a switch...
				//Could be placed before
				//Determines wich algorithm the item belongs to
				if (item == item_A) {
					computingState = COMPUTING_A;
					selectedLabels = labels_A;
				} else if (item == item_B) {
					computingState = COMPUTING_B;
					selectedLabels = labels_B;
				} else if (item == item_AB) {
					computingState = COMPUTING_AB;
					selectedLabels = labels_AB;
				}
				if (computingState == COMPUTING_A) {
					notifyNodeMarked(item.getNode());
				} else if (computingState == COMPUTING_B) {
					notifyNodeMarked(item.getNode());
				} else if (computingState == COMPUTING_AB) {
					notifyMergedNodeMarked(item.getNode());
				}

				//Update destination reached states
				if (item.getNode() == target) {
					if (computingState == COMPUTING_AB) {
						destinationReached = true;
					} else if (computingState == COMPUTING_A) {
						destinationReachedA = true;

					} else if (computingState == COMPUTING_B) {
						destinationReachedB = true;
					}


				} else  {
					for (Arc arc : item.getNode().getSuccessors()) {
						if (data.isAllowed(arc)) {
							LabelStar suiv = (LabelStar) selectedLabels.get(arc.getDestination());

							if (suiv == null) {
								if (computingState == COMPUTING_A) {
									suiv = new LabelStar(arc.getDestination(), Double.POSITIVE_INFINITY, directCostB);//Conserve the same heuristic BT
								} else if (computingState == COMPUTING_B) {
									suiv = new LabelStar(arc.getDestination(), Double.POSITIVE_INFINITY, directCostA);//Conserve the heuristic AT
								} else {
									suiv = new MergeLabel(arc.getDestination(), Double.POSITIVE_INFINITY, getInputData(), MergeLabel.MergingState.MERGED);
									//Heuristic of merged labels is set to distance to target. See MergeLabel
								}
								selectedLabels.put(arc.getDestination(), suiv);
							}


							if (suiv.getState() != Label.LabelState.MARKED /*|| computingState == COMPUTING_AB*/) {//MergeLabels can be updated even when marked
								double d = evalDist(item, arc);
								if (d < suiv.getCost()) {
									suiv.setCost(d);
									suiv.setPrev(arc);
									if (suiv.getState() == Label.LabelState.VISITED /*|| suiv.getState()==LabelState.MARKED*/) {//MergeLabels can be updated even when marked
										dijkstrAStarHeap.insertOrUpdate(suiv);
									} else {
										notifyNodeReached(suiv.getNode());
										suiv.setState(Label.LabelState.VISITED);
										dijkstrAStarHeap.insert(suiv);
									}
									
								}

							}
						}


					}
				}
			}
		}



		if (labels_AB.get(target) == null) { //Target is not reachable
			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.INFEASIBLE);
		} else {
			//Construct paths

			//Initialize nodes array for paths instanciation
			ArrayList<Node> pathArrayA = new ArrayList<>(); //AO
			ArrayList<Node> pathArrayB = new ArrayList<>(); //BO
			ArrayList<Node> pathArrayAB = new ArrayList<>(); //OT

			//Start tracking path the paths
			Arc cursorA;//Cursor tracking AO path
			Arc cursorB;//Cursor following BO path
			Arc cursorAB = labels_AB.get(target).getPrev(); //Cursor following OT Path

			//Start tracking the paths
			pathArrayAB.add(target);//Start with OT
			while (cursorAB != null) { //While start of path OT not reached
				pathArrayAB.add(0, cursorAB.getOrigin());
				cursorAB = labels_AB.get(cursorAB.getOrigin()).getPrev(); //Move cursor to previous node
			}

			cursorA = labels_A.get(pathArrayAB.get(0)).getPrev(); //First node of path OT is present in both labels set
			pathArrayA.add(pathArrayAB.get(0));//O belongs to AO path
			while (cursorA != null) { //While start of path AO not reached
				pathArrayA.add(0, cursorA.getOrigin());
				cursorA = labels_A.get(cursorA.getOrigin()).getPrev();//Move cursor to previous node
			}

			cursorB = labels_B.get(pathArrayAB.get(0)).getPrev(); //First node of path OT is present in both labels set
			pathArrayB.add(pathArrayAB.get(0));//O belongs to BO path
			while (cursorB != null) { //While start ogf path BO not reached
				pathArrayB.add(0, cursorB.getOrigin());
				cursorB = labels_B.get(cursorB.getOrigin()).getPrev(); //Move cursor to previous node
			}

			//Instanciate the paths according to choosen mode
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
