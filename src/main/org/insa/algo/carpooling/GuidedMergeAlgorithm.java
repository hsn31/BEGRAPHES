package org.insa.algo.carpooling;

import java.util.ArrayList;
import java.util.HashMap;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution;
import org.insa.algo.carpooling.MergeLabel.MergingState;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.Label;
import org.insa.algo.shortestpath.Label.LabelState;
import org.insa.algo.shortestpath.LabelStar;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class GuidedMergeAlgorithm extends CarPoolingAlgorithm {

	private static final int COMPUTING_A=0;
	private static final int COMPUTING_B=1;
	private static final int COMPUTING_AB=2;


	protected GuidedMergeAlgorithm(CarPoolingData data) {
		super(data);
	}

	protected Label newLabel(Node node,double cost,CarPoolingData data) {
		return new Label(node,cost);
	}

	private double evalDist(Label from, Arc arc,int computingState) {
		return from.getCost() + data.getCost(arc);
	}
	@Override
	protected CarPoolingSolution doRun() {


		Graph graph = getInputData().getGraph();

		Node startA = getInputData().getUser_A();
		Node startB = getInputData().getUser_B();
		Node target = getInputData().getDestination();

		System.out.println("Launching direct paths computing");

		double directCostA=new AStarAlgorithm(new ShortestPathData(graph, startA,target,getInputData().getArcInspector())).run().getCost();
		double directCostB=new AStarAlgorithm(new ShortestPathData(graph, startB,target,getInputData().getArcInspector())).run().getCost();



		System.out.println("MERGE LAUNCHED");
		System.out.println("A : "+startA.getId());
		System.out.println("B : "+startB.getId());
		System.out.println("T : "+target.getId());


		HashMap<Node, Label> labels_A = new HashMap<>();
		HashMap<Node, Label> labels_B = new HashMap<>();
		HashMap<Node, Label> labels_AB = new HashMap<>();

		labels_AB.put(target, new MergeLabel(target,directCostA+directCostB,getInputData(),true,true,MergingState.MERGED));



		BinaryHeap<Label> heapA = new BinaryHeap<>();
		BinaryHeap<Label> heapB = new BinaryHeap<>();
		BinaryHeap<Label> heapAB = new BinaryHeap<>();

		BinaryHeap [] binaryHeaps = new BinaryHeap[] {heapA,heapB,heapAB};

		Label startLabelA = newLabel(startA, directCostB,getInputData());
		Label startLabelB = newLabel(startB,directCostA,getInputData());
		labels_A.put(startA, startLabelA);
		heapA.insert(startLabelA);
		labels_B.put(startB,startLabelB);



		boolean destinationReached = false;
		int computingState=0;


		//DIJKSTRA ATB
		System.out.println("DIJKSTRA ABT LAUNCHED");
		while (!destinationReached && (heapA.size() > 0) || heapB.size()>0 || heapAB.size()>0) {
			BinaryHeap<Label> selectedHeap=null;
			HashMap<Node,Label> selectedLabels=null;
			destinationReached=true;
			double minimum=Double.POSITIVE_INFINITY;
			for(BinaryHeap<Label> heap :binaryHeaps) {
				if(heap.size()>0 && heap.findMin().getCost()<minimum && heap.findMin().getCost()<labels_AB.get(target).getCost()) {
					destinationReached=false;
					minimum=heap.findMin().getCost();
					selectedHeap=heap;
				}
			}
			if(selectedHeap==heapA) {
				computingState=COMPUTING_A;
				selectedLabels=labels_A;
				
			}
			else if(selectedHeap==heapB) {
				computingState=COMPUTING_B;
				selectedLabels=labels_B;
			}
			else if(selectedHeap==heapAB) {
				computingState=COMPUTING_AB;
				selectedLabels=labels_AB;
			}
			if(!destinationReached) {
				Label item = selectedHeap.deleteMin();
				if(selectedHeap==heapA) {
					notifyNodeMarked(item.getNode());
				}
				else if(selectedHeap==heapB) {
					notifyNodeMarked(item.getNode());
				}
				else if(selectedHeap==heapAB) {
					notifyMergedNodeMarked(item.getNode());
				}
				item.setState(Label.LabelState.MARKED);
				
				if (item.getNode() == target) {
				
				} else {
					for (Arc arc : item.getNode().getSuccessors()) {
						if (data.isAllowed(arc)) {
							Label suiv=selectedLabels.get(arc.getDestination());

							if (suiv == null) {
								suiv = newLabel(arc.getDestination(), Double.POSITIVE_INFINITY,getInputData());
								selectedLabels.put(arc.getDestination(), suiv);
							}


							if (suiv.getState() != Label.LabelState.MARKED) {
								double d = evalDist(item, arc,computingState);
								if (d < suiv.getCost()) {
									suiv.setCost(d);
									suiv.setPrev(arc);
									if (suiv.getState() == Label.LabelState.VISITED) {
										selectedHeap.remove(suiv);
									} else {
										suiv.setState(Label.LabelState.VISITED);
									}
									selectedHeap.insert(suiv);
									if(computingState==COMPUTING_A && labels_B.get(suiv.getNode())!=null && labels_B.get(suiv.getNode()).getState()==LabelState.MARKED){
										MergeLabel mergeLabel;
										if(labels_AB.get(suiv.getNode())==null) {
										mergeLabel = new MergeLabel(suiv.getNode(), Double.POSITIVE_INFINITY, getInputData(), true, true,MergingState.MERGED);
										labels_AB.put(suiv.getNode(),mergeLabel);
										}
										mergeLabel=(MergeLabel)labels_AB.get(suiv.getNode());
										if(mergeLabel.getCost()>suiv.getCost()-directCostA+labels_B.get(suiv.getNode()).getCost()-directCostB) {
											mergeLabel.setCost(suiv.getCost()-directCostA+labels_B.get(suiv.getNode()).getCost()-directCostB);
											heapAB.insertOrUpdate(mergeLabel);
										}
										notifyNodeMerged(suiv.getNode());
						
										
									}
									if(computingState==COMPUTING_B && labels_A.get(suiv.getNode())!=null && labels_A.get(suiv.getNode()).getState()==LabelState.MARKED){
										MergeLabel mergeLabel;
										if(labels_AB.get(suiv.getNode())==null) {
										mergeLabel = new MergeLabel(suiv.getNode(), Double.POSITIVE_INFINITY, getInputData(), true, true,MergingState.MERGED);
										labels_AB.put(suiv.getNode(),mergeLabel);
										}
										mergeLabel=(MergeLabel)labels_AB.get(suiv.getNode());
										if(mergeLabel.getCost()>suiv.getCost()-directCostB+labels_A.get(suiv.getNode()).getCost()-directCostA) {
											mergeLabel.setCost(suiv.getCost()-directCostB+labels_A.get(suiv.getNode()).getCost()-directCostA);
											heapAB.insertOrUpdate(mergeLabel);
										}
						
										
									}
									notifyNodeMerged(suiv.getNode());
								}
							}
						}
					}
				}
			}
		}
		System.out.println("DIJKSTRA A FINISHED");



		if (labels_AB.get(target) == null) {
			System.out.println("IMPOSSIBLE");
			return new CarPoolingSolution(getInputData(), AbstractSolution.Status.INFEASIBLE);
		} else {
			System.out.println("Constructing path");
			ArrayList<Node> pathArrayA = new ArrayList<>();
			ArrayList<Node> pathArrayB = new ArrayList<>();
			ArrayList<Node> pathArrayAB = new ArrayList<>();
			Arc cursorA;
			Arc cursorB;
			Arc cursorAB = labels_AB.get(target).getPrev();
			System.out.println("Constructing COM");
			pathArrayAB.add(target);
			while (cursorAB != null) {
				pathArrayAB.add(0, cursorAB.getOrigin());
				cursorAB = labels_AB.get(cursorAB.getOrigin()).getPrev();
			}

			cursorA = labels_A.get(pathArrayAB.get(0)).getPrev();
			pathArrayA.add(pathArrayAB.get(0));
			System.out.println("Constructing A");
			while (cursorA != null) {
				pathArrayA.add(0, cursorA.getOrigin());
				cursorA = labels_A.get(cursorA.getOrigin()).getPrev();
			}

			cursorB = labels_B.get(pathArrayAB.get(0)).getPrev();
			pathArrayB.add(pathArrayAB.get(0));
			System.out.println("Constructing B");
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
