package org.insa.algo.carpooling;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.carpooling.MergeLabel.MergingState;
import org.insa.algo.shortestpath.Label;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MergeAlgorithm extends CarPoolingAlgorithm {
	public MergeAlgorithm(CarPoolingData data) {
		super(data);
	}

	protected Label newLabel(Node node,double cost,CarPoolingData data) {
		return new Label(node,cost);
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

		if(startA == startB &&  startB == target){//No need to move, return void paths
			return new CarPoolingSolution(getInputData(),AbstractSolution.Status.OPTIMAL,new Path(graph,startA),new Path(graph,startB),new Path(graph,target));
		}


		HashMap<Node, Label> labels_A = new HashMap<>(); //Labels corresponding to the Dijkstra launched from A
		HashMap<Node, Label> labels_B = new HashMap<>(); //Labels corresponding to the Dijkstra launched from B
		HashMap<Node, MergeLabel> labels_AB = new HashMap<>(); //Labels corresponding to the AStar launched from the merging points

		BinaryHeap<Label> dijkstraHeap = new BinaryHeap<>();//Heap used for the Dijkstras computation(reset after the first Dijsktra)

		BinaryHeap<Label> aStarHeap = new BinaryHeap<>();//Heap used for the AStar computation(starts being updated during Dijkstra B computation)

		//Initialize first Dijkstra starts from A
		Label startLabel = newLabel(startA, 0,getInputData());
		labels_A.put(startA, startLabel);
		dijkstraHeap.insert(startLabel);
		boolean destinationReached = false;

		//DIJKSTRA A
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
						if (suiv == null) { //label wasn't reached until this point
							suiv = newLabel(arc.getDestination(), Double.POSITIVE_INFINITY,getInputData());
							labels_A.put(arc.getDestination(), suiv);
						}
						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {//suiv was already in the heap
									dijkstraHeap.remove(suiv);
								} else {//suiv was instanciated just before
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
		//END DIJKSTRA A : All nodes closer to A than T are now labeled and marked in labelsA

		//Initialize second Dijkstra, starts from B
		dijkstraHeap = new BinaryHeap<>();
		startLabel = newLabel(startB, 0,getInputData());
		labels_B.put(startB, startLabel);
		dijkstraHeap.insert(startLabel);
		destinationReached=false;

		//DIJKSTRA B
		while (!destinationReached && dijkstraHeap.size() > 0) {
			Label item = dijkstraHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyNodeMarked(item.getNode());
			Label item_A = labels_A.get(item.getNode());
			if (item_A != null && item_A.getState() == Label.LabelState.MARKED) { //Available for merging

				//Instanciate new Merging Label at node J with cost AJ+BJ
				MergeLabel mergeLabel = new MergeLabel(item.getNode(), item.getCost() + item_A.getCost(), getInputData(),MergingState.MERGED);
				mergeLabel.setState(Label.LabelState.VISITED);
				labels_AB.put(item.getNode(), mergeLabel);
				aStarHeap.insert(mergeLabel); //Start to fill AStar Heap
				notifyNodeMerged(item.getNode());//See CarpoolingObserver
			}
			//Following is identical to first Dijkstra
			if (item.getNode() == target) {
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {

					if (data.isAllowed(arc)) {

						Label suiv = labels_B.get(arc.getDestination());
						if (suiv == null) {
							suiv = newLabel(arc.getDestination(), Double.POSITIVE_INFINITY,getInputData());
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
		//END DIJKSTRA B : All nodes closer to B than T are now labeled and marked in labelsB

		//All nodes available for merging are now labeled and set to visited in the AStar heap

		//AStar is thus initialized, just set destinationReachrd to false
		destinationReached = false;

		//A STAR OT
		//Same as Dijkstra, only difference is in the labels
		while (!destinationReached && aStarHeap.size() > 0) {
			MergeLabel item = (MergeLabel) aStarHeap.deleteMin();
			item.setState(Label.LabelState.MARKED);
			notifyMergedNodeMarked(item.getNode());
			if (item.getNode() == target) {
				destinationReached = true;
			} else {
				for (Arc arc : item.getNode().getSuccessors()) {
					if (data.isAllowed(arc)) {
						MergeLabel suiv = labels_AB.get(arc.getDestination());
						if (suiv == null) {
							suiv = new MergeLabel(arc.getDestination(), Double.POSITIVE_INFINITY, getInputData(),MergingState.MERGED);
							labels_AB.put(arc.getDestination(), suiv);
						}

						if (suiv.getState() != Label.LabelState.MARKED) {
							double d = evalDist(item, arc);
							if (d < suiv.getCost()) {
								suiv.setCost(d);
								suiv.setPrev(arc);
								if (suiv.getState() == Label.LabelState.VISITED) {
									aStarHeap.remove(suiv);
								} else {
									notifyNodeReached(suiv.getNode());
									suiv.setState(Label.LabelState.VISITED);
								}
								aStarHeap.insert(suiv);
							}
						}
					}
				}
			}

		}

		if (labels_AB.get(target) == null) { //Target is not reachable
			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.INFEASIBLE);
		} else {
			//Initialize nodes array for paths instanciation
			ArrayList<Node> pathArrayA = new ArrayList<>(); //AO
			ArrayList<Node> pathArrayB = new ArrayList<>(); //BO
			ArrayList<Node> pathArrayAB = new ArrayList<>(); //OT

			//Start tracking path the paths
			Arc cursorA;//Cursor tracking AO path
			Arc cursorB;//Cursor following BO path
			Arc cursorAB = labels_AB.get(target).getPrev(); //Cursor following OT Path

			pathArrayAB.add(target);//Start with OT
			while (cursorAB != null) {//While start of path OT not reached
				pathArrayAB.add(0, cursorAB.getOrigin());
				cursorAB = labels_AB.get(cursorAB.getOrigin()).getPrev();//Move cursor to previous node
			}
			
			cursorA = labels_A.get(pathArrayAB.get(0)).getPrev();//First node of path OT is present in both labels set
			pathArrayA.add(pathArrayAB.get(0));//O belongs to AO path
			while (cursorA != null) {//While start of path AO not reached
				pathArrayA.add(0, cursorA.getOrigin());
				cursorA = labels_A.get(cursorA.getOrigin()).getPrev();//Move cursor to previous node
			}

			cursorB = labels_B.get(pathArrayAB.get(0)).getPrev(); //First node of path OT is present in both labels set
			pathArrayB.add(pathArrayAB.get(0));//O belongs to BO path
			while (cursorB != null) {//While start ogf path BO not reached
				pathArrayB.add(0, cursorB.getOrigin());
				cursorB = labels_B.get(cursorB.getOrigin()).getPrev();//Move cursor to previous node
			}

			//Instanciate the paths according to choosen mode
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
